//package com.example.travelbuddy.ui.screens.profile
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.travelbuddy.R
//
//@Composable
//fun ProfileScreen(
//    navController: NavController
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Profile picture
//        Image(
//            painter = painterResource(id = R.drawable.profile_placeholder), // Sostituisci con la tua immagine
//            contentDescription = "Profile picture",
//            modifier = Modifier
//                .size(120.dp)
//                .clip(CircleShape),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Name
//        Text(
//            text = "John Smith",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "john.smith@gmail.com",
//            fontSize = 16.sp,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = { /* Naviga alla schermata di modifica profilo */ },
//            modifier = Modifier.fillMaxWidth(0.6f)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Edit,
//                contentDescription = "Edit",
//                modifier = Modifier.size(18.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Edit Profile")
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Card(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Text(
//                    text = "Profile Details",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 16.dp))
//                )
//
//                ProfileDetailItem("Name", "John Smith")
//                ProfileDetailItem("Email", "john.smith@email.com")
//                ProfileDetailItem("Phone", "+1234567890")
//                ProfileDetailItem("Location", "Milan, Italy")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(
//            onClick = { /* Gestisci logout */ },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.errorContainer,
//                contentColor = MaterialTheme.colorScheme.error
//            ),
//            modifier = Modifier.fillMaxWidth(0.6f)
//        ) {
//            Text("Log Out")
//        }
//    }
//}
//
//@Composable
//fun ProfileDetailItem(label: String, value: String) {
//    Column(modifier = Modifier.padding(vertical = 8.dp)) {
//        Text(
//            text = label,
//            fontSize = 14.sp,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//        )
//        Text(
//            text = value,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}