package com.example.yijiali_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


//In this assignment, I utilize Radio Button and drop down menu as my additional component.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                ShoppingListApp()
            }
        }
    }
}

enum class QuantityType {
    NUMBER,
    SIZE
}

data class ShoppingItem(
    val name: String,
    val quantityType: QuantityType
) {
    var isChecked by mutableStateOf(false)
    var quantityNumber: Int? = null
    var quantitySize: String? = null
    var quantityUnit: String? = null
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    val shoppingList = remember { mutableStateListOf<ShoppingItem>() }
    var itemName by remember { mutableStateOf("") }

    var selectedQuantityType by remember { mutableStateOf(QuantityType.NUMBER) }
    var quantityNumber by remember { mutableStateOf("") }
    var quantitySize by remember { mutableStateOf("") }

    // Unit selection for Size
    val sizeUnits = listOf("lb", "kg", "g", "oz", "l", "ml", "pcs")
    var expanded by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf(sizeUnits.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Shopping List",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Select Quantity Type:")
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedQuantityType == QuantityType.NUMBER,
                onClick = { selectedQuantityType = QuantityType.NUMBER }
            )
            Text(
                text = "Number",
                modifier = Modifier
                    .clickable { selectedQuantityType = QuantityType.NUMBER }
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedQuantityType == QuantityType.SIZE,
                onClick = { selectedQuantityType = QuantityType.SIZE }
            )
            Text(
                text = "Size",
                modifier = Modifier
                    .clickable { selectedQuantityType = QuantityType.SIZE }
                    .padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedQuantityType) {
            QuantityType.NUMBER -> {
                TextField(
                    value = quantityNumber,
                    onValueChange = { quantityNumber = it },
                    label = { Text("Quantity (Number)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            QuantityType.SIZE -> {
                TextField(
                    value = quantitySize,
                    onValueChange = { quantitySize = it },
                    label = { Text("Quantity (Size)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sizeUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (itemName.isNotBlank()) {
                    val newItem = ShoppingItem(name = itemName, quantityType = selectedQuantityType)

                    when (selectedQuantityType) {
                        QuantityType.NUMBER -> {
                            if (quantityNumber.isNotBlank()) {
                                newItem.quantityNumber = quantityNumber.toIntOrNull()
                            }
                        }
                        QuantityType.SIZE -> {
                            if (quantitySize.isNotBlank()) {
                                newItem.quantitySize = quantitySize
                                newItem.quantityUnit = selectedUnit
                            }
                        }
                    }

                    val quantityValid = when (selectedQuantityType) {
                        QuantityType.NUMBER -> newItem.quantityNumber != null
                        QuantityType.SIZE -> !newItem.quantitySize.isNullOrBlank() && !newItem.quantityUnit.isNullOrBlank()
                    }

                    if (quantityValid) {
                        shoppingList.add(newItem)
                        itemName = ""
                        quantityNumber = ""
                        quantitySize = ""
                        selectedUnit = sizeUnits.first()
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Item")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the Shopping List
        LazyColumn {
            items(shoppingList) { item ->
                ShoppingListItem(item = item)
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { isChecked ->
                item.isChecked = isChecked
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium
            )
            val quantityText = when (item.quantityType) {
                QuantityType.NUMBER -> "Quantity: ${item.quantityNumber ?: "N/A"}"
                QuantityType.SIZE -> {
                    val unit = item.quantityUnit ?: ""
                    "Quantity: ${item.quantitySize ?: "N/A"} $unit"
                }
            }
            Text(
                text = quantityText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ShoppingListTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}