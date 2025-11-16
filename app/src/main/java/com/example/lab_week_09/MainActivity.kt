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
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

// -------------------------------------------------------
// 2. DATA MODEL
// -------------------------------------------------------
data class Student(
    var name: String
)

// -------------------------------------------------------
// 3. PARENT COMPOSABLE (STATE HOLDER)
// -------------------------------------------------------
@Composable
fun Home() {

    // LIST STATE
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    // INPUT FIELD STATE
    var inputField = remember {
        mutableStateOf(Student(""))
    }

    HomeContent(
        listData,
        inputField.value,
        onInputValueChange = { input ->
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        }
    )
}

// -------------------------------------------------------
// 4. CHILD COMPOSABLE (UI CONTENT)
// -------------------------------------------------------
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) {

    LazyColumn {

        // INPUT FORM
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = {
                        onInputValueChange(it)
                    }
                )

                Button(
                    onClick = { onButtonClick() }
                ) {
                    Text(text = stringResource(id = R.string.button_click))
                }
            }
        }

        // LIST OF STUDENTS
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = item.name)
            }
        }
    }
}

// OPTIONAL PREVIEW
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home()
    }
}
