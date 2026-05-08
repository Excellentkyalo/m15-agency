package com.example.edusphere.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager // ✅ Added Import
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.ui.components.PrimaryGradient
import kotlinx.coroutines.launch

data class OnboardingPage(val title: String, val desc: String, val icon: String)

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    val pages = listOf(
        OnboardingPage("Learn Smarter", "Access notes, assignments, and timetables in one place.", "📚"),
        OnboardingPage("Track Progress", "Real-time analytics on your performance and grades.", "📊"),
        OnboardingPage("Stay Connected", "Get instant announcements from admins and teachers.", "📢")
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryGradient)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // ✅ FIXED: Added HorizontalPager to enable Swiping
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(3f) // Give pager space to render
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = pages[page].icon, fontSize = 80.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = pages[page].title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = pages[page].desc,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Page Indicator
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 12.dp else 8.dp)
                            .background(
                                color = if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.4f),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        // Swipe to next page
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        // Navigate to Login
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Next" else "Get Started",
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    OnboardingScreen(onNavigateToLogin = {})
}