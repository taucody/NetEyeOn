package com.example.neteyeon.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.screens.CGUScreen
import com.example.neteyeon.components.AcceptCguCheckbox
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun CGUScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var acceptCgu by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Conditions Générales d'Utilisation")

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = """
                        Lorem Ipsum is simply dummy text of the printing and typesetting industry. 
                        Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, 
                        when an unknown printer took a galley of type and scrambled it to make a type specimen book. 
                        It has survived not only five centuries, but also the leap into electronic typesetting, 
                        remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets 
                        containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker 
                        including versions of Lorem Ipsum.
                    """.trimIndent()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AcceptCguCheckbox(
                content = "J'ai lu et j'accepte les CGU",
                checked = acceptCgu,
                onCheckedChange = { acceptCgu = it }
            )

            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked,
                enabled = acceptCgu
            ) {
                Text("Continuer")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CGUScreenPreview() {
    NetEyeOnTheme {
        CGUScreen(onContinueClicked = {})
    }
}