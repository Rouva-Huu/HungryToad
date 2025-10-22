package com.example.hungrytoad.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import java.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hungrytoad.utils.ZodiacUtils
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(onRegistrationComplete: (Player) -> Unit) {
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
                    Text("OK", style = MaterialTheme.typography.bodySmall)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Отмена", style = MaterialTheme.typography.bodySmall)
                }
            }
        ) {
            Box(
                modifier = Modifier.scale(0.8f)
            ) {
                DatePicker(
                    state = datePickerState,
                    title = {
                        Text(
                            "Выберите дату рождения",
//                            modifier = Modifier.padding(5.dp),
                            color = DarkGreen
                        )
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Регистрация игрока", style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 20.dp, start = 10.dp)
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = PaleGreen,
                    unfocusedLabelColor = PaleGreen,
                    unfocusedContainerColor = Nude,
                    focusedContainerColor = Nude
                )
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = LightNude)
            ) {
                Text(
                    "Пол",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
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
                                .padding(16.dp),
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
            }
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
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = PaleGreen,
                        unfocusedLabelColor = PaleGreen,
                        unfocusedContainerColor = Nude,
                        focusedContainerColor = Nude
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Выберите курс", style = MaterialTheme.typography.bodyMedium) }
                )

                ExposedDropdownMenu(
                    expanded = courseExpanded,
                    onDismissRequest = { courseExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.92f).background(LightNude)
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
                "Дата рождения: ${birthDate.get(Calendar.DAY_OF_MONTH)}." +
                        "${birthDate.get(Calendar.MONTH) + 1}." +
                        "${birthDate.get(Calendar.YEAR)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    showDatePicker = true
                },
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Выбрать дату рождения", style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(0.dp)
            ) {
                Text(
                    "Знак зодиака: $zodiacSign", style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Image(
                    painter = painterResource(ZodiacUtils.getZodiacIconResource(zodiacSign)),
                    contentDescription = zodiacSign,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(start = 10.dp)
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = LightNude)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Уровень сложности",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$difficultyLevel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGreen
                        )
                    }
                    Slider(
                        value = sliderValue,
                        onValueChange = { onDifficultyChanged(it) },
                        valueRange = 1f..5f,

                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = PaleGreen,
                            activeTrackColor = PaleGreen,
                            inactiveTrackColor = Marsh
                        )
                    )
                }
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
                    onRegistrationComplete(player)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Зарегистрироваться", style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }
    }
}
