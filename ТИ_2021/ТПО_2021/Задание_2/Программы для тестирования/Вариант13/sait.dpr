program lab4;

{$APPTYPE CONSOLE}

uses Windows;

procedure ReadCheck(var x:integer);
var
    ioerr : boolean;
begin
{$I-}
ioerr:=false;
repeat
if ioerr then
  writeln('Vi vveli dannie nepodhodaschego tipa, povtorite vvod!');
  readln(x);
  ioerr := true;
until IOResult = 0;
end;

var a:array[1..100000] of integer;
    d,x,y,z,n,i,max,n1,n2:integer;
    o:boolean;

begin
write ('Vvedite kol-vo elementov N ');
o:=false;
repeat
ReadCheck(N);
  if n>1 then
    o:=true
  else
    writeln('Kol-vo elementov dolzhno bit bolshe 1, povtorite vvod!');
until o;
o:=false;
Write('Dla ruchnogo zapolneliya massiva vvedite 0, dla randomnogo - 1 ');
repeat
ReadCheck(x);
  if x=0 then begin
    for i:=1 to N do  begin
      Write('Vvedite element ','A[',i,']=');
      ReadCheck(a[i]);
    end;
  o:=true;
  end else if x=1 then begin
  write('Vvedite diapazon elementov massiva ');
  repeat
  ReadCheck(D);
    if d>=0 then
      o:=true
    else
      writeln('Diapazon dolzhen bit polozhitelnim, povtorite vvod!');
  until o;
  randomize;
    For  i:=1 to N do begin
      A[i]:=Random(d);
      write(a[i],' ');
    end;
  end else writeln('Nyzno vvesti 0 libo 1, povtorite vvod!');
until o;
writeln;
////
max:=0;
n1:=1;
n2:=1;
for x:=1 to n-1 do begin
  for y:=x+1 to n do begin
    o:=true;
    for z:=0 to (y-x) div 2 do begin
      o:=a[x+z]=a[y-z];
      if o = false then Break;
    end;
    if o and (y-x>max-1) then begin
      n1:=x;
      n2:=y;
      max:=y-x+1;
    end;
  end;
end;
if max>0 then begin
SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),
  FOREGROUND_GREEN or FOREGROUND_INTENSITY);
writeln ('Max = ',max);
for x:=n1 to n2 do write (a[x],' ');
end else begin
SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),
  FOREGROUND_RED or FOREGROUND_INTENSITY);
writeln ('Takih posledovatelnostei net!');
end;
readln;
end.
