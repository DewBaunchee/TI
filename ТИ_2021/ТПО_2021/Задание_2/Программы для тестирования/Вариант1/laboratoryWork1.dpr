{ Дано натуральное число N. С помощью алгоритма "Решето Эратосфена"
найти четверки меньших N простых чисел, принадлежащих одному
десятку(например, 11, 13, 17, 19).
}


program laboratoryWork1;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var DynA: array of integer;
       N, i, j, k : integer;
       N_check : boolean;

begin

writeln('Welcome to the first laboratory work!!!');
writeln;

N_check := false;

repeat
  write('Enter N (N >=  10) : ');
  readln(N);

  if N < 10 then  writeln('You entered an invalid number. Please, try again.')
            else N_check := true;
  writeln;          
until N_check = true;

k := 0;
setLength(DynA, N);

for i := 1 to N do
  DynA[i] := i;
writeln;

write('Initial array: ');
for i := 1 to N do
  write(DynA[i], ' ');
writeln; writeln;

DynA[1] := 0;

for i := 2 to N do
  for j := i + 1 to N do
    if DynA[j] mod i = 0 then DynA[j] := 0;

write('Array with only prime numbers: ');
for i := 1 to N do
  write(DynA[i], ' ');
writeln; writeln;

writeln('Result: ');
for i := 1 to N div 10 do
  begin
    for j := (i-1)*10 to (i*10)-1 do
       if DynA[j] > 0 then inc(k);
    if k = 4 then
      begin
        for j := (i-1)*10 to (i*10)-1 do
          if DynA[j] > 0 then write(DynA[j],' ');
        writeln;
      end;
    k := 0;
  end;

writeln;
writeln('Press Enter to exit...');
readln;

end.
