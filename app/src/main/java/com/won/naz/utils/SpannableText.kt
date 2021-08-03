package com.won.naz.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import androidx.core.content.res.ResourcesCompat
import com.won.naz.R

object SpannableText {
    fun convertToSpannable(text: String, start: Int, end: Int, color: Int, bold: Boolean, size: Int): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (bold) {
            spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        spannableString.setSpan(AbsoluteSizeSpan(size, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    fun setSpans(spannableString: SpannableString, start: Int, end: Int, color: Int, bold: Boolean, size: Int): SpannableString {
        spannableString.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (bold) {
            spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        spannableString.setSpan(AbsoluteSizeSpan(size, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    fun setClockSpans(spannableString: SpannableString, start: Int, end: Int, context: Context): SpannableString {
        val myType = Typeface.create(ResourcesCompat.getFont(context, R.font.gsansmedium), Typeface.NORMAL)
        spannableString.setSpan(TypefaceSpan(myType), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }
}