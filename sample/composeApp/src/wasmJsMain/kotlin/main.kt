import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.intsoftdev.datetimewheelpicker.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  CanvasBasedWindow("datetime-wheel-picker") {
    App()
  }
}