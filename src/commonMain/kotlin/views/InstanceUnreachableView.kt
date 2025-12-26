package views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import components.BaseScaffold
import infrastructure.ErrorHandler
import view_models.InstanceUnreachableViewModel

@Composable
fun InstanceUnreachableView(
    navController: NavHostController,
    errorHandler: ErrorHandler,
    viewModel: InstanceUnreachableViewModel = viewModel { InstanceUnreachableViewModel(navController) }
) {
    BaseScaffold(
        errorHandler = errorHandler
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(150.dp)
            )
            Text(
                text = "Instance unreachable!",
                style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.error),
            )
            if(viewModel.instanceUrl != null) {
                Text(
                    text = buildAnnotatedString {
                        append("Instance url: ")
                        withLink(LinkAnnotation.Url(url = viewModel.instanceUrl)) {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline)
                            ) {
                                append(viewModel.instanceUrl)
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Button(
                onClick = {
                    viewModel.retry()
                },
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Text("Retry")
                }
            }
        }
    }
}