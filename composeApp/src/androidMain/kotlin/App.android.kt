import androidx.compose.runtime.Composable
import com.example.tasklist.ui.TaskViewModel
import org.koin.compose.koinInject

@Composable
actual fun App() {
    val viewModel = koinInject<TaskViewModel>()
    AppContent(viewModel)
}
