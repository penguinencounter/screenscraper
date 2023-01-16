## TerminalState
```
boolean color
boolean compress
boolean empty?
true: 
    varint width
    varint height
    varint length
    compressed buffer
false:
    (nothing)
```

## compressed buffer
```
int cursorX
int cursorY
boolean cursorBlink
nybble cursorBackgroundColor
nybble cursorColor
for each line do:
    for each character do:
        byte character
    for each character do:
        nybble background color
        nybble foreground color
for each palette color do:
    byte channel 1 (red)
    byte channel 2 (green)
    byte channel 3 (blue)
```
