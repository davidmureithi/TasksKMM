import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.tasklist.ui.TaskScreen
import com.example.tasklist.ui.TaskViewModel

@Composable
expect fun App()

@Composable
internal fun AppContent(viewModel: TaskViewModel) {
    MaterialTheme {
        TaskScreen(viewModel = viewModel)
    }
}
