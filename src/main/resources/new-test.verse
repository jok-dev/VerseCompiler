Print<native>(message : []char) : void

Sqrt<native>(X : float) : float

VariableTest : string = "Test"

Main(Args : []string) : int = {
    Print("Hello World {VariableTest}")

    Sqrt(12.0)
    Print("Sqrt(12) = {X}")

}

