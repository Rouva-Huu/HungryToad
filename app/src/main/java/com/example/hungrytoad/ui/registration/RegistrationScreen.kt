package com.example.hungrytoad.ui.registration

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import java.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hungrytoad.utils.ZodiacUtils
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavController
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.theme.DarkGreen
import com.example.hungrytoad.ui.theme.LightNude
import com.example.hungrytoad.ui.theme.Marsh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var courseExpanded by remember { mutableStateOf(false) }
    var difficultyLevel by remember { mutableStateOf("Нормально") }
    var sliderValue by remember { mutableStateOf(3f) }
    var birthDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс")
    val genders = listOf("Мужской", "Женский")
    fun getDifficultyString(level: Float): String {
        return when (level.toInt()) {
            1 -> "Очень легко"
            2 -> "Легко"
            3 -> "Нормально"
            4 -> "Сложно"
            5 -> "Очень сложно"
            else -> "Неизвестно"
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthDate.timeInMillis
    )

    fun onDifficultyChanged(newValue: Float) {
        sliderValue = newValue
        difficultyLevel = getDifficultyString(newValue)
    }

    val zodiacSign = ZodiacUtils.getZodiacSign(
        birthDate.get(Calendar.DAY_OF_MONTH),
        birthDate.get(Calendar.MONTH) + 1
    )

    if (showDatePicker) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = LightNude
            ),
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            birthDate = calendar
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text("Выберите дату рождения", modifier = Modifier.padding(20.dp), color = DarkGreen)
                },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    headlineContentColor = DarkGreen,
                    containerColor = LightNude,
                    yearContentColor = DarkGreen,
                    navigationContentColor = DarkGreen
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Регистрация игрока", style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 50.dp, start = 10.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("ФИО", style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Пол", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            genders.forEach { gender ->
                Row(
                    Modifier
                        .selectable(
                            selected = (gender == selectedGender),
                            onClick = { selectedGender = gender },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (gender == selectedGender),
                        onClick = null
                    )
                    Text(
                        text = gender,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Text("Курс", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = courseExpanded,
            onExpandedChange = { courseExpanded = !courseExpanded }
        ) {
            OutlinedTextField(
                value = selectedCourse,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded)
                },
                colors = OutlinedTextFieldDefaults.colors(),
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text("Выберите курс", style = MaterialTheme.typography.bodyMedium) }
            )

            DropdownMenu(
                expanded = courseExpanded,
                onDismissRequest = { courseExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            selectedCourse = course
                            courseExpanded = false
                        }
                    )
                }
            }
        }

        Text(
            "Уровень сложности: $difficultyLevel",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = sliderValue,
            onValueChange = { onDifficultyChanged(it) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(thumbColor = Marsh,
                activeTrackColor = Marsh,
                inactiveTrackColor = LightNude
            )
        )

        Text(
            "Дата рождения: ${birthDate.get(Calendar.DAY_OF_MONTH)}." +
                    "${birthDate.get(Calendar.MONTH) + 1}." +
                    "${birthDate.get(Calendar.YEAR)}",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                showDatePicker = true
            },
            modifier = Modifier.fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Выбрать дату рождения", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp))
        }
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Знак зодиака: $zodiacSign", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically))
            Image(
                painter = painterResource(ZodiacUtils.getZodiacIconResource(zodiacSign)),
                contentDescription = zodiacSign,
                modifier = Modifier
                    .size(64.dp)
                    .padding(start = 18.dp)
            )
        }
        Button(
            onClick = {
                val player = Player(
                    fullName = fullName,
                    gender = selectedGender,
                    course = selectedCourse,
                    difficultyLevel = difficultyLevel,
                    birthDate = birthDate,
                    zodiacSign = zodiacSign
                )

                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "playerData",
                    player
                )
                navController.navigate("results")
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Зарегистрироваться", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp))
        }
    }
}
