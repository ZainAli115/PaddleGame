package com.example.paddlegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.alpha
import com.example.paddlegame.ui.theme.PaddleGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gameView = findViewById<PaddleGameView>(R.id.gameView)
    }
}

class PaddleGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val PADDLE_WIDTH = 200
        const val PADDLE_HEIGHT = 30
        const val BALL_RADIUS = 20
        const val BALL_SPEED = 10
    }

    private var paddleX: Int = 0
    private var paddleY: Int = 0
    private var ballX: Int = 0
    private var ballY: Int = 0
    private var ballSpeedX: Int = BALL_SPEED
    private var ballSpeedY: Int = BALL_SPEED
    private var counter: Int = 0
    private var isGameOver: Boolean = false

    private val paddlePaint = Paint().apply {
        color = Color.rgb(0, 204, 204)
        isAntiAlias = true
    }

    private val ballPaint = Paint().apply {
        color = Color.rgb(255, 255, 102)
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        color = Color.rgb(55, 71, 79) // Dark teal color for background
    }

    private val gameOverPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val counterPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val handler = Handler()

    private val gameLoop: Runnable = object : Runnable {
        override fun run() {
            update()
            invalidate()
            handler.postDelayed(this, 1000 / 30)
        }
    }

    init {
        paddleX = 0
        paddleY = 0
        ballX = 0
        ballY = 0
        ballSpeedX = BALL_SPEED
        ballSpeedY = BALL_SPEED

        handler.postDelayed(gameLoop, 1000 / 30)
    }

    private fun update() {
        if (!isGameOver) {
            ballX += ballSpeedX
            ballY += ballSpeedY

            if (ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH && ballY >= height - PADDLE_HEIGHT - BALL_RADIUS) {
                ballSpeedY = -BALL_SPEED
                counter++ // Increment counter when ball hits paddle
            }

            if (ballX <= 0 || ballX >= width) {
                // Reverse ball direction when hitting side walls
                ballSpeedX = -ballSpeedX
            }

            if (ballY <= 0) {
                // Reverse ball direction when hitting top wall
                ballSpeedY = -ballSpeedY
            }

            if (ballY >= height) {
                // Game over condition: Ball misses paddle and hits the bottom
                isGameOver = true
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw paddle with gradient
        val paddleGradient = LinearGradient(
            paddleX.toFloat(), paddleY.toFloat(), (paddleX + PADDLE_WIDTH).toFloat(), (paddleY + PADDLE_HEIGHT).toFloat(),
            Color.rgb(0, 204, 204), Color.rgb(0, 102, 102), Shader.TileMode.CLAMP
        )
        paddlePaint.shader = paddleGradient
        canvas.drawRect(
            paddleX.toFloat(), (height - PADDLE_HEIGHT).toFloat(),
            (paddleX + PADDLE_WIDTH).toFloat(), height.toFloat(), paddlePaint
        )

        // Draw ball with shadow
        ballPaint.setShadowLayer(10f, 0f, 0f, Color.BLACK)
        canvas.drawCircle(ballX.toFloat(), ballY.toFloat(), BALL_RADIUS.toFloat(), ballPaint)

        // Display counter
        val counterText = "Score: $counter"
        gameOverPaint.alpha = 191
        counterPaint.alpha=50

        val counterTextWidth = gameOverPaint.measureText(counterText)
        val counterTextX = 150f // Set left padding
        val counterTextY = 100f // Set top padding
        canvas.drawText(counterText, counterTextX, counterTextY, counterPaint)

        // Display game over prompt with background
        if (isGameOver) {
            val rectF = RectF((width / 4).toFloat(), (height / 3).toFloat(), (width * 3 / 4).toFloat(), (height * 2 / 3).toFloat())
            canvas.drawRoundRect(rectF, 20f, 20f, backgroundPaint)
            canvas.drawText("Game Over. Try Again?", (width / 2).toFloat(), (height / 2).toFloat(), gameOverPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                paddleX = event.x.toInt() - PADDLE_WIDTH / 2
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (isGameOver) {
                    // Reset game
                    resetGame()
                }
            }
        }
        return true
    }

    private fun resetGame() {
        paddleX = 0
        paddleY = 0
        ballX = 0
        ballY = 0
        ballSpeedX = BALL_SPEED
        ballSpeedY = BALL_SPEED
        counter = 0
        isGameOver = false
        invalidate()
    }
}


