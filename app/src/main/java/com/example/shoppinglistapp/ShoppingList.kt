
package com.example.shoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id:Int,
    var name: String,
    var quantity:Int,
    var isEditing:Boolean=false
)

@Composable
fun ShoppingListApp()
{
    var itemsList by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember{ mutableStateOf(false) }
    var itemName by remember{ mutableStateOf("") }
    var itemQuantity by remember{ mutableStateOf("") }

    val numbers:List<Int> = listOf(1,2,3);
    val doubled:List<Int> = numbers.map { it*2 };

    Column(modifier= Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        Button(onClick = { showDialog=true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Text("Add Item");
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp))
        {
            items(itemsList) {
                item-> // make it become item
                if(item.isEditing){
                    // when editing item we don't know where it is in the list. I think that mutableState works only on list so we have to map whole list
                    // in order for state to work and refresh the ui.
                    // then we find edited item by using find on itemsList
                    ShoppingItemEditor(item = item, onEditComplete = {
                        editedName,editedQuantity->itemsList = itemsList.map { it.copy(isEditing = false) }
                        item.isEditing=false;
                        val editedItem = itemsList.find { it.id==item.id }

                        editedItem?.let {
                            //it.isEditing=false;
                            it.name=editedName;
                            it.quantity=editedQuantity;

                        }
                    })
                }
                else
                {
                    ShoppingListItem(item = item,
                        onEditClick = { itemsList = itemsList.map { it.copy(isEditing = it.id==item.id) } },
                        onDeleteClick = { itemsList-=item; })
                }
            }
        }
    }

    if(showDialog)
    {
        AlertDialog(onDismissRequest = { showDialog=false },
            confirmButton = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = {
                                    if(itemName.isNotBlank())
                                    {
                                        val newItem = ShoppingItem(itemsList.size+1,itemName,itemQuantity.toInt())
                                        itemsList+=newItem;
                                        showDialog = false;
                                        itemName = ""
                                        itemQuantity=""
                                    }
                                }) {
                                    Text(text = "Add")
                                }
                                Button(onClick = { showDialog=false }) {
                                    Text(text = "Cancel")
                                }
                            }
            },
            title = {Text("Add Shopping item")},
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {itemName=it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                        )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = {itemQuantity=it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}
@Composable
fun ShoppingListItem(
    item:ShoppingItem,
    onEditClick:()->Unit,
    onDeleteClick:()->Unit
)
{
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = item.name,
            Modifier.padding(8.dp));
        Text(text = "Qty: ${item.quantity}",
            Modifier.padding(8.dp));
        Row(modifier = Modifier.padding(8.dp))
        {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit,contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete,contentDescription = null)
            }
        }
    }
}
@Composable
fun ShoppingItemEditor(item:ShoppingItem,onEditComplete:(String,Int)->Unit)
{
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier
        .fillMaxWidth()
        //.background(Color.White)
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly

    ){
        Column {

            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                //textStyle = TextStyle(color = Color.Black)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                //textStyle = TextStyle(color = Color.Black)
            )
        }
        Button(
            onClick = {
                isEditing=false;
                onEditComplete(editedName,editedQuantity.toIntOrNull()?:1);
            }
        ) {
            Text(text = "Save")
        }
    }

}
