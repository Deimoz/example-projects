@file:Suppress("LocalVariableName", "NonAsciiCharacters")

package csc.makrobot.dsl

import csc.markobot.api.*
import csc.markobot.dsl.*
import csc.markobot.api.LoadClass.*
import csc.markobot.dsl.Materials.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestsPositive {

    @Test
    fun testNonDSL() {
        val робот = MakroBot("Wall-E",
            Head(Plastic(2), listOf(LampEye(10), LampEye(10), LedEye(3)), Mouth(Speaker(3))),
            Body(Metal(1), listOf("I don't want to survive.", "I want live.")),
            Hands(Plastic(3), LoadClass.Light, LoadClass.Medium),
            Chassis.Caterpillar(10)
        )
        verify(робот)
    }

    @Test
    fun testDSL() {
        val робот = robot("Wall-E") {
            head {
                plastic thickness 2

                eyes {
                    lamps {
                        amount = 2
                        illumination = 10
                    }
                    leds {
                        amount = 1
                        illumination = 3
                    }
                }

                mouth {
                    speaker {
                        power = 3
                    }
                }
            }

            body {
                metal thickness 1

                strings {
                    +"I don't want to survive."
                    +"I want live."
                }
            }

            hands {
                plastic thickness 3
                load = Light - Medium
            }

            chassis = caterpillar width 10
        }

        verify(робот)
    }

    @Test
    fun testDSLOtherChassis() {
        val роботНаКолесах = robot("Wall-E") {
            head {
                plastic thickness 2

                eyes {
                    lamps {
                        amount = 2
                        illumination = 10
                    }
                }

                mouth {
                    speaker {
                        power = 3
                    }
                }
            }

            body {
                metal thickness 1
            }

            hands {
                plastic thickness 3
                load = Light - Medium
            }
            chassis = wheels {
                diameter = 4
                number = 2
            }
        }

        Assertions.assertEquals(Chassis.Wheel(2, 4), роботНаКолесах.chassis)

        val роботНаНогах = robot("Wall-E") {
            head {
                plastic thickness 2

                eyes {
                    lamps {
                        amount = 2
                        illumination = 10
                    }
                }

                mouth {
                    speaker {
                        power = 3
                    }
                }
            }

            body {
                metal thickness 1
            }

            hands {
                plastic thickness 3
                load = Light - Medium
            }
            chassis = legs
        }

        Assertions.assertEquals(Chassis.Legs, роботНаНогах.chassis)
    }

    private fun verify(робот: MakroBot) {
        Assertions.assertEquals("Wall-E", робот.name)
        Assertions.assertEquals(Plastic(2), робот.head.material)
        Assertions.assertArrayEquals(arrayOf(LampEye(10), LampEye(10), LedEye(3)), робот.head.eyes.toTypedArray())
        Assertions.assertEquals(Mouth(Speaker(3)), робот.head.mouth)

        Assertions.assertEquals(Metal(1), робот.body.material)
        Assertions.assertArrayEquals(arrayOf("I don't want to survive.", "I want live."), робот.body.strings.toTypedArray())

        Assertions.assertEquals(Plastic(3), робот.hands.material)
        Assertions.assertEquals(LoadClass.Light, робот.hands.minLoad)
        Assertions.assertEquals(LoadClass.Medium, робот.hands.maxLoad)

        Assertions.assertEquals(Chassis.Caterpillar(10), робот.chassis)
    }
}
