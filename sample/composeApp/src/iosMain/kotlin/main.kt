import androidx.compose.ui.window.ComposeUIViewController
import com.intsoftdev.datetimewheelpicker.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
