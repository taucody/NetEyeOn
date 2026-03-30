package com.example.neteyeon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetEyeOnTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var shouldShowOnboarding by remember { mutableStateOf(true) }
    var shouldShowAllowing by remember { mutableStateOf(false) }

    Surface(modifier = modifier) {
        when {
            shouldShowOnboarding -> {
                OnboardingScreen(
                    onContinueClicked = { shouldShowOnboarding = false }
                )
            }

            !shouldShowAllowing -> {
                CGUScreen(
                    onContinueClicked = { shouldShowAllowing = true }
                )
            }

            else -> {
                Allowing()
            }
        }
    }
}

@Composable
private fun Greetings(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Greeting("Android")
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mini_logo),
            contentDescription = "Logo NETeyeON",
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 16.dp)
        )
        Text("NETeyeON")
        Text("network scanner")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continuer")
        }
    }
}

@Composable
fun CGUScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier) {

    var acceptCgu by remember {mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Conditions Générales d'Utilisation")

            Spacer(modifier = Modifier.height(16.dp))
            Surface(color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(horizontal = 24.dp)) {

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
                onCheckedChange = {acceptCgu = it}
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
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }
}

@Composable
fun Allowing(modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Text(
            text = "NETeyeON a besoin de votre autorisation",
            modifier = modifier
        )
    }
}

@Composable
fun AcceptCguCheckbox(
    content: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text = content)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NetEyeOnTheme {
        MyApp()
    }
}

@Preview(showBackground = true)
@Composable
fun AllowingPreview() {
    NetEyeOnTheme {
        Allowing()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    NetEyeOnTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingsPreview() {
    NetEyeOnTheme {
        Greetings()
    }
}

@Preview
@Composable
fun MyAppPreview() {
    NetEyeOnTheme {
        MyApp(Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
fun CGUScreenPreview() {
    NetEyeOnTheme {
        CGUScreen(onContinueClicked = {})
    }
}