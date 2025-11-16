package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.example.lab_week_09.ui.theme.*
import androidx.compose.foundation.layout.statusBarsPadding

// DATA MODEL
data class Student(
    var name: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    App(navController)
                }
            }
        }
    }
}

// ROOT NAVIGATION
@Composable
fun App(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home { jsonList ->
                navController.navigate("resultContent/?listData=$jsonList")
            }
        }

        composable(
            "resultContent/?listData={listData}"
        ) {
            val json = it.arguments?.getString("listData").orEmpty()
            ResultContent(json)
        }
    }
}

// HOME SCREEN (STATE HOLDER)
@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {

    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    var inputField by remember {
        mutableStateOf(Student(""))
    }

    HomeContent(
        listData,
        inputField,
        onInputValueChange = { input ->
            inputField = inputField.copy(name = input)
        },
        onButtonClick = {
            // FIX PROBLEM 1 — prevent empty submit
            if (inputField.name.isNotBlank()) {
                listData.add(inputField)
                inputField = Student("")
            }
        },
        navigateFromHomeToResult = {

            // BONUS — convert to JSON using Moshi
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, Student::class.java)
            val adapter = moshi.adapter<List<Student>>(type)
            val json = adapter.toJson(listData.toList())

            navigateFromHomeToResult(json)
        }
    )
}

// HOME CONTENT UI
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {
    LazyColumn {

        item {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = onInputValueChange
                )

                Row {

                    // Disable button when input empty → FIX problem #1
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click),
                        enabled = inputField.name.isNotBlank()
                    ) {
                        onButtonClick()
                    }

                    PrimaryTextButton(
                        text = "Finish"
                    ) {
                        navigateFromHomeToResult()
                    }
                }
            }
        }

        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// RESULT PAGE (BONUS)
@Composable
fun ResultContent(jsonList: String) {

    // Decode JSON → List<Student>
    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)
    val studentList = adapter.fromJson(jsonList) ?: emptyList()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OnBackgroundTitleText(text = "Result Page")

        LazyColumn {
            items(studentList) { s ->
                OnBackgroundItemText(text = s.name)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        val nav = rememberNavController()
        App(nav)
    }
}
