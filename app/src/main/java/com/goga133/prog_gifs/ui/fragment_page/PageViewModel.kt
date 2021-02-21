package com.goga133.prog_gifs.ui.fragment_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goga133.prog_gifs.business_logic.NetworkService
import com.goga133.prog_gifs.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * ViewModel для [PageFragment]
 */
class PageViewModel : ViewModel() {
    /**
     * Выбранная секция для зарузки.
     */
    var pageSection: PageSection = PageSection.RANDOM

    /**
     * Неизменяемая LiveData для фрагмента.
     */
    val gifState: LiveData<State<Gif?>>
        get() = _gifState


    /**
     * Неизменяемая LiveData для фрагмента.
     */
    val panel: LiveData<Panel>
        get() = _panel

    /**
     * Неизменяемая LiveData информацинной панели.
     */
    val infoPanel: LiveData<Boolean>
        get() = _infoPanel

    /**
     * Текущее состояние изображения. См. [State]
     */
    private val _gifState = MutableLiveData<State<Gif?>>()

    /**
     * Текущее состояние панели. См. [Panel]
     *
     * Изначально Panel принимает все False значения.
     */
    private var _panel: MutableLiveData<Panel> = MutableLiveData<Panel>().apply {
        this.postValue(Panel(backButton = false, nextButton = false, refreshButton = false))
    }

    /**
     * Текущий индекс страницы. Изначально -1.
     */
    private var currentIndex = -1


    /**
     * Текущее состояние информационной панели.
     */
    private val _infoPanel = MutableLiveData<Boolean>()

    /**
     * Текущее количество страниц, которое можно загрузить. Изначально -1.
     */
    private var totalCount = -1

    /**
     * Текущая страница для загрузки. Никак не используется при [PageSection.RANDOM]
     */
    private var currentPage = 1

    /**
     * Изменяемый [MutableList] со всеми загруженными гифками.
     *
     * Реализация через [MutableSet] не подходит из-за того, что [PageSection.RANDOM] может сломаться,
     * если гифка уже была подгружена. Эта проблема решалась бы через обработку каждой случайной гифки,
     * но зачем...
     */
    private val loadedGIFs = mutableListOf<Gif>()

    /**
     * Блок инициализации для логгирования.
     */
    init {
        Timber.plant(Timber.DebugTree())
    }

    /**
     * Метод для инициализации. Если загрузка первая [isFirstLoad], а такое может быть только 1 раз:
     * когда загрузки ещё не было, либо она была первой и при этом неудачной.
     *
     * Если инициализация уже прошла, то ничего не происходит.
     */
    fun initLoad() {
        if (isFirstLoad()) {
            Timber.d("First loading...")
            nextGif()
        }
    }

    /**
     * Метод для повторной загрузки гифки.
     * Если кнопка работает - обновляется, то есть запускается загрузка [loadGifByPageSection]
     */
    fun refresh() {
        if (_panel.value?.refreshButton == true) {
            loadGifByPageSection()
        }
    }

    /**
     * Метод для открытия/закрытия информационной панели.
     */
    fun doInfoPanel(){
        if(_infoPanel.value != true){
            _infoPanel.postValue(true)
        }
        else{
            _infoPanel.postValue(false)
        }
    }

    /**
     * Метод для загрузки следующей гифки.
     * Если можно загрузить следующию, либо загрузка первая [isFirstLoad], тогда
     * проверяется нужна загрузки [needLoadGif], если загружать из сети не нужно,
     * то берём уже из загруженных.
     *
     */
    fun nextGif() {
        if (hasNext() || isFirstLoad()) {
            Timber.d("Load a next gif...")
            if (needLoadGif()) {
                loadGifByPageSection()
            } else {
                // Передвигаем текщий индекс на 1.
                currentIndex += 1

                // Говорим панели, что рефреш не нужен, а кнопки принимаю соответствующие значения.
                postPanel(backButton = !isFirstCurrentGIF(), nextButton = hasNext())
                // Уведомляем о новом состоянии:
                postState(Event.SUCCESS, gif = loadedGIFs.elementAt(currentIndex))
            }
        } else {
            Timber.w("Unable to upload next gif.")
        }
    }

    /**
     * Метод для загрузки предыдущей гифки.
     * Загрузка происходит только в случае, если эта гифка не первая [isFirstCurrentGIF]
     */
    fun prevGif() {
        if (!isFirstCurrentGIF()) {
            Timber.d("Load a previous gif...")
            if (_panel.value?.refreshButton == false) {
                currentIndex -= 1
            }
            // Говорим панели, что рефреш не нужен, а кнопки принимаю соответствующие значения.
            postPanel(!isFirstCurrentGIF(), true)
            // Уведомляем о новом состоянии:
            postState(Event.SUCCESS, throwable = null, gif = loadedGIFs.elementAt(currentIndex))
        } else {
            Timber.w("Unable to upload previous gif.")
        }
    }

    /**
     * Приватный метод для уведомлении об ошибке связанной с загрузкой.
     */
    private fun onFailed(throwable: Throwable) {
        Timber.e(throwable)

        postPanel(backButton = !isFirstCurrentGIF(), refreshButton = true)
        postState(Event.ERROR, throwable)
    }

    /**
     * Приватный метод для уведомления об успешной загрузке.
     */
    private fun onSuccess() {
        currentIndex += 1
        currentPage += 1

        Timber.i("Successful load. New element by index: $currentIndex")

        postPanel(backButton = !isFirstCurrentGIF(), nextButton = hasNext())
        postState(Event.SUCCESS, gif = loadedGIFs.elementAt(currentIndex))
    }

    /**
     * Метод для загрузки случайной гифки. Загрузка считается успешной,
     * если ответ 200 и гифка notNull. Все остальное - неуспешная загрузка.
     */
    private fun loadRandomGif() {
        if (pageSection != PageSection.RANDOM) {
            Timber.e(
                "Происходит загрузка случайной гифки, в то время когда section = %s",
                pageSection
            )
        }

        return NetworkService.retrofitService().getRandomGif().enqueue(object : Callback<Gif> {
            override fun onResponse(call: Call<Gif>, response: Response<Gif>) {
                val randomGif = response.body()

                if (response.code() == 200 && randomGif != null) {
                    loadedGIFs.add(randomGif)
                    onSuccess()
                } else {
                    onFailed(Throwable("Ответ сервера: ${response.code()}; randomGif = $randomGif"))
                }
            }

            override fun onFailure(call: Call<Gif>, t: Throwable) {
                onFailed(t)
            }
        })
    }

    /**
     * Метод для загрузки гифок. Загрузка считается успешной,
     * если ответ 200 и список гифок не пуст. Все остальное - неуспешная загрузка.
     */
    private fun loadGIFs() {
        if (pageSection == PageSection.RANDOM) {
            Timber.e(
                "Происходит загрузка гифок, в то время когда выбран раздел со случайными"
            )
        }

        return NetworkService.retrofitService()
            .getSectionGIFs(section = pageSection.toString(), page = currentPage)
            .enqueue(object : Callback<ResponseWrapper> {
                override fun onResponse(
                    call: Call<ResponseWrapper>,
                    response: Response<ResponseWrapper>
                ) {
                    val responseWrapper = response.body()
                    if (response.code() == 200 && responseWrapper != null &&
                        !responseWrapper.result.isNullOrEmpty()
                    ) {
                        loadedGIFs.addAll(responseWrapper.result)
                        totalCount = responseWrapper.totalCount
                        onSuccess()
                    } else {
                        onFailed(
                            Throwable(
                                "Ответ сервера: ${response.code()}; " + "res = ${responseWrapper?.result}"
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
                    onFailed(t)
                }
            })
    }

    /**
     * Метод выбирает по нужному разделу нужный метод для загрузки и уведомляет всех,
     * что началась загрузка.
     */
    private fun loadGifByPageSection() {
        _panel.postValue(Panel(backButton = false, nextButton = false, refreshButton = false))
        postState(Event.LOADING, throwable = null, gif = null)

        return when (pageSection) {
            PageSection.RANDOM -> loadRandomGif()
            else -> loadGIFs()
        }
    }

    /**
     * Если список пуст и текущий индекс ещё не дошёл до конца списка,
     * значит загрузка не требуется.
     */
    private fun needLoadGif(): Boolean {
        return loadedGIFs.isEmpty() || loadedGIFs.size - currentIndex <= 1
    }

    /**
     * Гифка считается первой, если её индекс не больше 0.
     */
    private fun isFirstCurrentGIF(): Boolean {
        return currentIndex <= 0
    }

    /**
     * Первой загрузкой считается такая, когда [currentIndex] = -1.
     */
    private fun isFirstLoad(): Boolean {
        return currentIndex == -1
    }

    /**
     * Если раздел случайных гифок - то следующий элемент можно подгрузить, если список не пуст.
     * Если другие разделы - то след. эл. можно подгрузить, если список не пуст,
     * и если [totalCount] больше чем [currentIndex] + 1
     */
    private fun hasNext(): Boolean {
        return when (pageSection) {
            PageSection.RANDOM -> loadedGIFs.isNotEmpty()
            else -> loadedGIFs.isNotEmpty() && totalCount - currentIndex > 1
        }
    }

    /**
     * Оповестить всех подписчиков, что произошли изменения в [gifState]
     */
    private fun postState(event: Event, throwable: Throwable? = null, gif: Gif? = null) {
        Timber.d(
            "Post was been updated. Event = %s, Throwable = %s, Data = %s",
            event, throwable.toString(), gif
        )

        _gifState.postValue(
            State(
                event = event,
                throwable = throwable,
                data = gif
            )
        )
    }

    /**
     * Оповестить всех подписчиков, что произошли изменения в [panel]
     */
    private fun postPanel(
        backButton: Boolean = false,
        nextButton: Boolean = false,
        refreshButton: Boolean = false
    ) {
        Timber.d(
            "Panel was been updated. Back Button = %s, Next Button = %s, Refresh Button = %s",
            backButton, nextButton, refreshButton
        )

        _panel.postValue(
            Panel(
                backButton = backButton,
                nextButton = nextButton,
                refreshButton = refreshButton
            )
        )
    }

}