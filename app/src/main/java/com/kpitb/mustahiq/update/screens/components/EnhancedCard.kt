package com.kpitb.mustahiq.update.screens.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.kpitb.mustahiq.R

@Composable
fun EnhancedCard(
    context: Context,
    navController: NavHostController,
    cardBackgroundColor: Color,
    imageTintColor: Color,
    @DrawableRes imageResId: Int,
    text: String,
    route: String
) {
    AnimatedClickable(
        context = context,
        soundResId = R.raw.click_sound,
        onClick = {
            navController.navigate(route)
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = Color.Gray.copy(alpha = 0.6f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .shadow(
                            2.dp,
                            CircleShape,
                            ambientColor = Color.Green.copy(alpha = 0.2f),
                            spotColor = Color.White.copy(alpha = 0.1f)
                        )
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(66.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(imageTintColor)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
