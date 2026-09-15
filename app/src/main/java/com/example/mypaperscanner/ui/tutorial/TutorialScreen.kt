package com.example.mypaperscanner.ui.tutorial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypaperscanner.ui.common.*
import com.example.mypaperscanner.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = BrandBackground,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { iteration ->
                        val active = pagerState.currentPage == iteration
                        Surface(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (active) 12.dp else 8.dp),
                            shape = CircleShape,
                            color = if (active) BrandBlue else BrandInk.copy(alpha = 0.2f),
                            border = if (active) androidx.compose.foundation.BorderStroke(2.dp, BrandInk) else null
                        ) {}
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onFinish) {
                        Text("Skip", color = BrandInk.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyLarge)
                    }

                    NeubrutalistButton(
                        modifier = Modifier.width(160.dp),
                        onClick = {
                            if (pagerState.currentPage < 3) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onFinish()
                            }
                        },
                        containerColor = if (pagerState.currentPage == 3) BrandGreen else BrandBlue,
                        borderRadius = 12.dp
                    ) {
                        Text(
                            if (pagerState.currentPage == 3) "Get Started" else "Next",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            TutorialSlide(page)
        }
    }
}

@Composable
fun TutorialSlide(pageIndex: Int) {
    val data = when (pageIndex) {
        0 -> TutorialData(
            title = "Welcome to MyPaperScanner!",
            description = "The friendliest way to digitize your world. Let's get you set up in seconds.",
            pose = MascotPose.WAVING
        )
        1 -> TutorialData(
            title = "Dual Format Support",
            description = "Save your scans as crisp PDFs or high-quality Images. You're in control of every pixel.",
            pose = MascotPose.CAMERA
        )
        2 -> TutorialData(
            title = "Everything Organized",
            description = "Your documents live in a beautiful gallery. Finding that one receipt has never been easier.",
            pose = MascotPose.SEARCHING
        )
        else -> TutorialData(
            title = "Instant Conversion",
            description = "Need to turn that image into a PDF? One tap is all it takes to switch formats anytime.",
            pose = MascotPose.HAPPY
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Mascot(pose = data.pose, modifier = Modifier.size(200.dp))
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineMedium,
            color = BrandInk,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = data.description,
            style = MaterialTheme.typography.bodyLarge,
            color = BrandInk.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}

data class TutorialData(
    val title: String,
    val description: String,
    val pose: MascotPose
)
