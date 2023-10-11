package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundedIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                // A surface container using the 'background' color from the theme
                myApp {
                    //TopHeader()
                    MainContent()
                }

            }
        }
    }

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
                      color = Color(0xFFE9D7F7)
                      //.clip(shape= RoundedCornerShape(corner = CornerSize(12.dp)))
    ) {
        Column(modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

            val total = "%.2f".format(totalPerPerson)

            Text(text = "Total Per Person", style = MaterialTheme.typography.headlineMedium)
            Text(text = "$$total", style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold)

        }
    }
}

@Composable
fun myApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipAppTheme {
        myApp {
            Text("Hello Again")
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){

    Column(modifier = Modifier.padding(all = 12.dp)) {

        BillForm(){ billAmt ->

            Log.d("AMT","$billAmt")

        }

    }



}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValueChanged: (String) -> Unit = {}){
    val totalBillstate = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillstate.value) {
        totalBillstate.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage =(sliderPositionState.value * 100).toInt()

    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }
    

    TopHeader(totalPerPerson = totalPerPerson.value)


    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)

    ) {
        Column(modifier = Modifier.
        padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            InputField(valueState = totalBillstate, labelId = "Enter Bill", enabled = true, isSingleLine = true,
                onAction = KeyboardActions{

                    if(!validState) return@KeyboardActions
                    //todo - OnValueChanged
                    onValueChanged(totalBillstate.value.trim())

                    keyboardController?.hide()
                }
            )
             if(validState) {
                 Row(
                     modifier = Modifier.padding(3.dp),
                     horizontalArrangement = Arrangement.Start
                 )
                 {
                     Text(text = "Split", modifier = Modifier.align(alignment = CenterVertically))
                     Spacer(modifier = Modifier.width(120.dp))
                     RoundedIconButton(
                         modifier = Modifier,
                         imageVector = Icons.Rounded.Remove,
                         onClick = {
                             if (splitByState.value > 1)
                                 splitByState.value = splitByState.value - 1
                             else 1

                             totalPerPerson.value =
                                 calculateTotalPerPerson(
                                     totalBill = totalBillstate.value.toDouble(),
                                     splitBy = splitByState.value,
                                     tipPercentage = (sliderPositionState.value * 100).toInt()
                                 )

                         })

                     Text(
                         text = "${splitByState.value}", modifier = Modifier
                             .padding(start = 9.dp, end = 9.dp)
                             .align(CenterVertically)
                     )

                     RoundedIconButton(
                         modifier = Modifier,
                         imageVector = Icons.Default.Add,
                         onClick = {
                             if (splitByState.value < range.last)
                                 splitByState.value = splitByState.value + 1
                             totalPerPerson.value =
                                 calculateTotalPerPerson(
                                     totalBill = totalBillstate.value.toDouble(),
                                     splitBy = splitByState.value,
                                     tipPercentage = (sliderPositionState.value * 100).toInt()
                                 )

                         })

                 }
                 Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 20.dp)) {
                     Text(text = "Tip", modifier = Modifier.align(CenterVertically))
                     Spacer(modifier = Modifier.width(200.dp))
                     Text(
                         text = "$ ${tipAmountState.value}",
                         modifier = Modifier.align(alignment = Alignment.CenterVertically)
                     )
                 }
                 Column(
                     verticalArrangement = Arrangement.Center,
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Text(text = "$tipPercentage %")
                     Spacer(modifier = Modifier.height(20.dp))
                     Slider(value = sliderPositionState.value,
                         onValueChange = { newValue ->
                             sliderPositionState.value = newValue

                             tipAmountState.value =
                                 calculateTotalTip(
                                     totalBill = totalBillstate.value.toDouble(),
                                     tipPercentage = (newValue * 100).toInt()
                                 )
                             totalPerPerson.value =
                                 calculateTotalPerPerson(
                                     totalBill = totalBillstate.value.toDouble(),
                                     splitBy = splitByState.value,
                                     tipPercentage = (newValue * 100).toInt()
                                 )

                         }, modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                         steps = 6,
                         onValueChangeFinished = {
                         })

                 }
             }
        else
                Box(){}

        }
    }

}









