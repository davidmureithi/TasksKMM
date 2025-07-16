import androidx.compose.runtime.Composable
import com.example.tasklist.ui.TaskViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Composable
actual fun App() {
    val koin = IosKoinHelper()
    AppContent(viewModel = koin.taskViewModel)
}

private class IosKoinHelper : KoinComponent {
    val taskViewModel: TaskViewModel = get()
}
