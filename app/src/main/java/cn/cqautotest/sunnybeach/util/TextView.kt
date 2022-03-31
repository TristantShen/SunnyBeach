package cn.cqautotest.sunnybeach.util

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

fun TextView.setDefaultEmojiParser() {
    filters = Array(1) { EmojiInputFilter() }
}

fun TextView.addDefaultEmojiParser() {
    val emojiInputFilter = EmojiInputFilter()
    filters = if (filters.isEmpty()) {
        // 如果没有表情输入过滤器，则添加一个
        Array(1) { emojiInputFilter }
    } else {
        // 如果已经有输入过滤器了，则在原有的输入过滤器之外额外添加一个表情输入过滤器
        val filterList = filters.toMutableList()
        filterList.add(emojiInputFilter)
        filterList.toTypedArray()
    }
}

fun TextView.clearText() {
    text = null
}

val TextView.textString
    get() = text.toString()

/**
 * TextView 装饰，支持追加 展开/收起 文字
 */
fun TextView.addViewMoreToTextView(
    text: Spanned,
    expandText: String,
    maxLine: Int,
    highlightColor: Int = Color.parseColor("#1D7DFA"),
    listener: View.OnClickListener
) {
    try {
        val textView = this
        textView.post {
            val truncatedSpannableString: SpannableStringBuilder
            val startIndex: Int
            if (textView.lineCount > maxLine) {
                val lastCharShown: Int = textView.layout.getLineVisibleEnd(maxLine - 1)
                val displayText =
                    text.substring(0, lastCharShown - expandText.length + 1) + " " + expandText
                startIndex = displayText.indexOf(expandText)
                truncatedSpannableString = SpannableStringBuilder(displayText)
                textView.text = truncatedSpannableString
                textView.movementMethod = LinkMovementMethod.getInstance()
                truncatedSpannableString.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        listener.onClick(widget)
                    }
                }, startIndex, startIndex + expandText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = truncatedSpannableString
                // This click event is not firing that's why we are adding click event for SpannableStringBuilder above.
                // textView.setOnClickListener(listener)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}