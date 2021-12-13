package csc.markobot.dsl

import csc.markobot.api.*
import csc.markobot.api.LoadClass.*
import csc.markobot.dsl.Materials.*

fun robot(name: String, operations: MakroBotBuilder.() -> Unit): MakroBot {
    return MakroBotBuilder(name).apply(operations).build()
}

@DslMarker
annotation class LayerBuildDsl

@LayerBuildDsl
interface Builder<T> {
    fun build(): T
}

class MakroBotBuilder(private val name: String = "robot") : Builder<MakroBot> {
    // dummy elements
    val legs = Chassis.Legs
    val caterpillar: Chassis.Caterpillar = Chassis.Caterpillar(1)

    // base initialization
    private var head: Head = HeadBuilder().build()
    private var body: Body = BodyBuilder().build()
    private var hands: Hands = HandsBuilder().build()
    var chassis: Chassis = legs

    override fun build(): MakroBot = MakroBot(name, head, body, hands, chassis)

    fun head(settings: HeadBuilder.() -> Unit) {
        head = HeadBuilder().apply(settings).build()
    }

    fun body(settings: BodyBuilder.() -> Unit) {
        body = BodyBuilder().apply(settings).build()
    }

    fun hands(settings: HandsBuilder.() -> Unit) {
        hands = HandsBuilder().apply(settings).build()
    }

    fun wheels(settings: WheelBuilder.() -> Unit) = WheelBuilder().apply(settings).build()

    infix fun Chassis.Caterpillar.width(width: Int) = Chassis.Caterpillar(width)

    interface HasMaterial {
        var material: Material

        infix fun Materials.thickness(thickness: Int) {
            material = when (this) {
                metal -> Metal(thickness)
                plastic -> Plastic(thickness)
            }
        }
    }

    class HeadBuilder(override var material: Material = Plastic(1)) : HasMaterial, Builder<Head> {
        private var eyes = listOf<Eye>()
        private var mouth = Mouth(null)

        override fun build() = Head(material, eyes, mouth)

        fun eyes(eyesBuilder: EyesBuilder.() -> Unit) {
            eyes = EyesBuilder().apply(eyesBuilder).eyes
        }

        fun mouth(settings: MouthBuilder.() -> Unit) {
            mouth = MouthBuilder().apply(settings).build()
        }

        class EyesBuilder {
            val eyes = mutableListOf<Eye>()

            fun lamps(eyesFactory: EyesFactory.() -> Unit) {
                createEyes(eyesFactory, ::LampEye)
            }

            fun leds(eyesFactory: EyesFactory.() -> Unit) {
                createEyes(eyesFactory, ::LedEye)
            }

            private fun createEyes(eyesFactory: EyesFactory.() -> Unit, cons: (Int) -> Eye) {
                val createdEyes = EyesFactory().apply(eyesFactory)
                createdEyes.repeatEyes(eyes, cons(createdEyes.illumination))
            }

            class EyesFactory(var amount: Int = 0, var illumination: Int = 0) {
                fun repeatEyes(eyes: MutableList<Eye>, eye: Eye) {
                    repeat(amount) {
                        eyes += eye
                    }
                }
            }
        }

        class MouthBuilder : Builder<Mouth> {
            private var speaker = SpeakerBuilder().build()

            override fun build() = Mouth(speaker)

            fun speaker(settings: SpeakerBuilder.() -> Unit) {
                speaker = SpeakerBuilder().apply(settings).build()
            }

            class SpeakerBuilder : Builder<Speaker> {
                var power = 1

                override fun build() = Speaker(power)
            }
        }
    }

    class BodyBuilder(override var material: Material = Plastic(1)) : HasMaterial, Builder<Body> {
        private var strings = listOf<String>()

        override fun build() = Body(material, strings)

        infix fun strings(text: StringsBlock.() -> Unit) {
            strings = StringsBlock().apply(text).text
        }

        class StringsBlock {
            private val strings = mutableListOf<String>()

            operator fun String.unaryPlus() {
                strings += this
            }

            val text: List<String> get() = strings
        }
    }

    class HandsBuilder(override var material: Material = Plastic(1)) : HasMaterial, Builder<Hands> {
        var load = Pair(VeryLight, VeryLight)

        override fun build() = Hands(material, load.first, load.second)

        operator fun LoadClass.minus(second: LoadClass): Pair<LoadClass, LoadClass> {
            return if (this > second) {
                throw IllegalArgumentException("First class of load should be equal or lower than the second one")
            } else {
                this to second
            }
        }
    }

    class WheelBuilder(var number: Int = 1, var diameter: Int = 1) : Builder<Chassis.Wheel> {
        override fun build() = Chassis.Wheel(number, diameter)
    }
}