package com.example.coll

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.coll.ui.ScheduleActivity
import com.example.coll.ui.SvgMapHelper
import com.example.coll.ui.SvgMapView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class MainActivity : AppCompatActivity() {

    // Этажи колледжа
    private enum class Floor(val displayName: String, val assetName: String) {
        BASEMENT("Подвал", "floor_basement.svg"),
        FLOOR1("1 этаж", "floor1.svg"),
        FLOOR2("2 этаж", "floor2.svg"),
        FLOOR3("3 этаж", "floor3.svg")
    }
    
    private lateinit var svgMapView: SvgMapView
    private lateinit var debugButton: MaterialButton
    private lateinit var floorTitleText: TextView
    private lateinit var floorToggleGroup: MaterialButtonToggleGroup
    private lateinit var debugText: TextView
    private lateinit var instructionText: TextView
    private var debugMode = false
    private var currentFloor: Floor = Floor.FLOOR2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        svgMapView = findViewById(R.id.svgMapView)
        debugButton = findViewById(R.id.debugButton)
        floorTitleText = findViewById(R.id.floorTitleText)
        floorToggleGroup = findViewById(R.id.floorToggleGroup)
        debugText = findViewById(R.id.debugText)
        instructionText = findViewById(R.id.instructionText)

        // Устанавливаем этаж по умолчанию (2 этаж)
        svgMapView.post {
            setFloor(Floor.FLOOR2)
        }

        svgMapView.onRoomClickListener = { roomId ->
            if (!debugMode) {
                openSchedule(roomId)
            }
        }

        svgMapView.onDebugClickListener = { svgX, svgY ->
            val message = "Координаты SVG: X=${svgX.toInt()}, Y=${svgY.toInt()}\n" +
                    "Используйте эти координаты в setupRoomAreas()\n\n" +
                    "Пример для прямоугольного кабинета:\n" +
                    "SvgMapHelper.createRectArea(\"room_XXX\", " +
                    "${(svgX - 50).toInt()}f, ${(svgY - 50).toInt()}f, " +
                    "${(svgX + 50).toInt()}f, ${(svgY + 50).toInt()}f)"
            debugText.text = message
            debugText.visibility = View.VISIBLE
            Log.d("SvgMap", "Click at SVG coordinates: X=$svgX, Y=$svgY")
        }

        debugButton.setOnClickListener {
            debugMode = !debugMode
            svgMapView.setDebugMode(debugMode)
            
            if (debugMode) {
                debugButton.text = "Выключить отладку"
                instructionText.text = "Кликайте на карте, чтобы увидеть координаты. Скопируйте их в setupRoomAreas()"
                debugText.visibility = View.VISIBLE
            } else {
                debugButton.text = "Режим отладки"
                instructionText.text = "Нажмите на кабинет для просмотра расписания"
                debugText.visibility = View.GONE
            }
        }

        // Обработчик переключения этажей
        floorToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val floor = when (checkedId) {
                R.id.btnFloorBasement -> Floor.BASEMENT
                R.id.btnFloor1 -> Floor.FLOOR1
                R.id.btnFloor2 -> Floor.FLOOR2
                R.id.btnFloor3 -> Floor.FLOOR3
                else -> Floor.FLOOR2
            }
            setFloor(floor)
        }

        // По умолчанию выбираем 2 этаж в переключателе
        floorToggleGroup.check(R.id.btnFloor2)
    }
    
    /**
     * Устанавливает текущий этаж: загружает SVG и настраивает области кабинетов
     */
    private fun setFloor(floor: Floor) {
        currentFloor = floor
        floorTitleText.text = floor.displayName

        try {
            svgMapView.loadSvgFromAssets(floor.assetName)
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка загрузки SVG для этажа $floor: ${e.message}", e)
        }

        setupRoomAreas(floor)
    }
    
    /**
     * Настраивает интерактивные области кабинетов на карте
     * Для каждого этажа можно задать свой набор кабинетов.
     *
     * ВАЖНО: Если координаты не совпадают, используйте режим отладки!
     * Включите режим отладки, кликните на кабинет и скопируйте координаты сюда.
     */
    private fun setupRoomAreas(floor: Floor) {
        val areas = mutableListOf<com.example.coll.data.RoomArea>()

        when (floor) {
            Floor.BASEMENT -> {
                // TODO: добавьте области кабинетов подвала, когда будут готовы координаты
                // Пример:
                // areas.add(SvgMapHelper.createRectArea("basement_room_1", x1, y1, x2, y2))
            }
            Floor.FLOOR1 -> {
                // TODO: добавьте области кабинетов 1 этажа
            }
            Floor.FLOOR2 -> {
                // Координаты основаны на структуре SVG 2 этажа (floor2.svg)
                // Пол: y=116.5..816.5, кабинеты снизу: y=585..816.5

                areas.add(SvgMapHelper.createRectArea("room_201", 213f, 585f, 380f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_202", 380f, 585f, 446.5f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_203", 446.5f, 585f, 495.5f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_204", 495.5f, 585f, 605.5f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_205", 653f, 585f, 802.5f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_206", 802.5f, 585f, 933.5f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_207", 933.5f, 585f, 1155f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_208", 1155f, 585f, 1285f, 816.5f))
                areas.add(SvgMapHelper.createRectArea("room_209", 1309.5f, 585f, 1521.5f, 816.5f))

                // Sports room (верхняя часть)
                areas.add(SvgMapHelper.createRectArea("room_sports", 331f, 116.5f, 575.5f, 585f))
            }
            Floor.FLOOR3 -> {
                // TODO: добавьте области кабинетов 3 этажа
            }
        }

        svgMapView.setRoomAreas(areas)
    }
    
    /**
     * Открывает экран с расписанием кабинета
     */
    private fun openSchedule(roomId: String) {
        val intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra(ScheduleActivity.EXTRA_ROOM_ID, roomId)
        startActivity(intent)
    }
}
