package com.goga133.fintech2021.data

/**
 * Панель, отвечающая за показ кнопок. True - показывать нужно, False - нет.
 */
data class Panel(val backButton : Boolean, val nextButton: Boolean, var refreshButton : Boolean)
