package com.goga133.prog_gifs.ui.fragment_page

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goga133.prog_gifs.R
import com.goga133.prog_gifs.business_logic.EventObserver
import com.goga133.prog_gifs.data.Event
import com.goga133.prog_gifs.data.Gif
import com.goga133.prog_gifs.data.PageInfo
import com.goga133.prog_gifs.databinding.FragmentPageBinding
import com.goga133.prog_gifs.business_logic.RequestDrawable
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Фрагмент для отображения гифки. Если гифку невозможно отобразить - отображается котик, то есть
 * главный друг любого разработчика.
 */
class PageFragment : Fragment(), EventObserver<Gif> {
    /**
     * Сообщение об ошибке для отображения в [Snackbar]
     */
    private val messageErrorSnackBarText by lazy {
        binding.root.resources.getString(R.string.error_snackbar_text)
    }

    /**
     * Сообщение об ошибке для отображения в описании гифки.
     */
    private val messageErrorGifText by lazy {
        binding.root.resources.getString(R.string.error_gif_text)
    }

    /**
     * Длительность плавного выплывания изображения. Настраивается через [PreferenceManager]
     * В случае ошибки используется [DEFAULT_ANIMATE_DURATION]
     */
    private var gifAnimateDuration: Int = DEFAULT_ANIMATE_DURATION

    // По примеру из документации
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel.
     */
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Достаём информацию о странице.
        val pageInfo = arguments?.getParcelable<PageInfo>(ARG_PAGE_INFO)

        // Инициализируем ViewModel
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            pageInfo?.let {
                this.pageSection = it.pageSection
            }
        }
        // Инициализируем логгер.
        Timber.plant(Timber.DebugTree())
    }

    /**
     * Инициализируем биндинг и привязываем смотрителей к LiveData
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(layoutInflater)

        pageViewModel.gifState.observe(viewLifecycleOwner) {
            when (it.event) {
                Event.ERROR -> onError(it.throwable)
                Event.SUCCESS -> onSuccess(it.data!!)
                Event.LOADING -> onLoading()
            }
        }

        // Выставляем смотрителя для панели кнопок.
        pageViewModel.panel.observe(viewLifecycleOwner) {
            binding.fabBack.isEnabled = it.backButton
            binding.fabNext.isEnabled = it.nextButton
            binding.fabRefresh.isEnabled = it.refreshButton
        }

        binding.cardView.animate().alpha(0.0f)
        pageViewModel.infoPanel.observe(viewLifecycleOwner){
            if(it){
                binding.cardView.animate().alpha(0.6f)
            }
            else{
                binding.cardView.animate().alpha(0.0f)
            }
        }

        // Выставляем кнопкам слушателей:
        binding.fabBack.setOnClickListener { pageViewModel.prevGif() }
        binding.fabNext.setOnClickListener { pageViewModel.nextGif() }
        binding.fabRefresh.setOnClickListener { pageViewModel.refresh() }
        binding.fabInfo?.setOnClickListener{pageViewModel.doInfoPanel()}
        // Проводим загрузку
        pageViewModel.initLoad()

        Timber.d("onCreateView was been successful")
        return binding.root
    }

    /**
     * Выставляем [gifAnimateDuration] из [PreferenceManager].
     */
    override fun onResume() {
        super.onResume()
        gifAnimateDuration = PreferenceManager.getDefaultSharedPreferences(binding.root.context)
            .getInt("duration", DEFAULT_ANIMATE_DURATION)

    }

    /**
     * Метод выполняется после успешной загрузки.
     * Отображение гиф изображения работает по следующему принципу:
     * 1. Индикатор загрузки отобразился, [Glide] пытается показать превью ([Gif.previewUrl])
     * 2. Дальше [Glide] пытается подгрузить gif-изображение, если оно имеется. (если нет см. п.3),
     * 3. Если gif-изображение неккоретно, либо произошла ошибка во время загрузки, то пользователь
     * увидит [Snackbar] с сообщением [messageErrorSnackBarText], а вместо gif-изображения будет
     * превью изображение. Смотри [showErrorSnackBar]
     *
     * [Glide] кэширует данные по стратегии [CACHE_STRATEGY]
     * [Glide] использует анимацию плавного отображения фотографии с задержкой [gifAnimateDuration]
     * Для корректного отображения используется подход "centerCrop"
     * @param data - текущий [Gif], который нужно отобразить на экране.
     */
    override fun onSuccess(data: Gif) {
        // Заполняем карточку:
        binding.textViewDescription.text = data.description
        "Дата: ${data.date}".also { binding.textViewDate.text = it }
        "Автор: ${data.author}".also { binding.textViewAuthor.text = it }
        binding.progressBar.visibility = View.VISIBLE

        Glide.with(binding.root)
            .load(data.gifUrl)
            .transition(GenericTransitionOptions.with { view ->
                view.alpha = 0f
                val fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
                fadeAnim.duration = gifAnimateDuration.toLong()
                fadeAnim.start()
            })
            .diskCacheStrategy(CACHE_STRATEGY)
            .thumbnail(Glide.with(binding.root).load(data.previewUrl))
            .listener(RequestListenerImpl(object : RequestDrawable {
                override fun onLoadFailed() {
                    binding.progressBar.visibility = View.GONE
                    showErrorSnackBar()
                }

                override fun onResourceReady() {
                    binding.progressBar.visibility = View.GONE
                }
            }))
            .error(R.drawable.error_cat)
            .centerCrop()
            .into(binding.imageViewGif)
    }

    /**
     * Метод выполняется во время загрузки гиф изображения.
     */
    override fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    /**
     * Метод выполняется после неудачной загрузки.
     * Гифка загружается с [R.drawable.error_cat]
     * Описание гифки - [messageErrorGifText]
     * Остальные поля чистятся.
     *
     * Отображется [Snackbar] с [messageErrorSnackBarText]. Смотри [showErrorSnackBar]
     * @param throwable - ошибка
     */
    override fun onError(throwable: Throwable?) {
        if(throwable == null){
            Timber.w("Throwable is null.")
        }
        else{
            Timber.d(throwable)
        }

        Glide.with(binding.root).load(R.drawable.error_cat).centerCrop()
            .into(binding.imageViewGif)

        binding.textViewDescription.text = messageErrorGifText
        binding.textViewDate.text = ""
        binding.textViewAuthor.text = ""
        binding.progressBar.visibility = View.GONE

        showErrorSnackBar()
    }

    /**
     * Если [View] [isVisible], тогда на экран показывается [Snackbar],
     * с текстом [messageErrorSnackBarText]
     */
    private fun showErrorSnackBar() {
        Timber.d("Был вызван метод showErrorSnackBar.")

        if (isVisible) {
            Snackbar.make(
                binding.root,
                messageErrorSnackBarText,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        /**
         * Аргумент для [Bundle] для хранения [PageInfo].
         */
        private const val ARG_PAGE_INFO = "page_info"

        /**
         * Создание экземпляра фрагемента с [Bundle],
         * в котором лежит [PageInfo] с аргументом [ARG_PAGE_INFO]
         *
         * @param pageInfo - информация о странице.
         */
        @JvmStatic
        fun newInstance(
            pageInfo: Parcelable
        ): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAGE_INFO, pageInfo)
                }
            }
        }

        /**
         * Дефольное значение анимации выплывания gif-изображения
         */
        const val DEFAULT_ANIMATE_DURATION: Int = 1000

        /**
         * Стратегия кэширования изображения для [Glide]
         */
        val CACHE_STRATEGY: DiskCacheStrategy = DiskCacheStrategy.ALL
    }
}