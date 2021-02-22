program laba444;

{$APPTYPE CONSOLE}

uses
  SysUtils;

const
  n=12;
var
  i,j,t,SizeB: integer;
  B: array[1..n] of integer;
  ioerr,read_check : boolean;
  vs:string;
begin
  read_check:=false;
  repeat
  writeln('Enter 1 for enter elements randomly  or 2 to enter elements by keyboard');
  readln(vs);
  vs:=lowercase(vs);
  if vs='1' then
  begin
    randomize;
    for i:=1 to n do
      b[i]:=random(5);
    read_check:=true;
  end
  else
    if vs='2' then
    begin
      for i := 1 to n do
      begin
        writeln('Enter B[',i,']');
        {$I-}
        ioerr:=false;
        repeat
          if ioerr=true then
          writeln('Error.Please,enter the data correctly');
          readln(b[i]);
          ioerr := true;
        until IOResult = 0;
        {$I+}
        read_check:=true;
      end;
    end
    else  writeln('Error.Please,enter the data correctly');
  until read_check=true;
  writeln('You array is ');
  for i:=1 to n do
    write(i,#255);
  writeln;
  for i:=1 to n do
    write(b[i],#255);
  writeln;
  I:=n;
  SizeB:=I;
  While i>0 do
  begin
    j:=i-1;
    While j>0 do
    begin
      If B[i]=B[j] then
      begin
        writeln('delete ',j,' element');
        for t:=j to SizeB do
          B[t]:=B[t+1];
        Dec(SizeB);
        Dec(i);
        for t:=1 to sizeB do
          write(t,#255);
        writeln;
        for t:=1 to sizeB do
          write(b[t],#255);
        writeln;
      end;
      Dec(j);
    end;
    Dec(i);
  end;
  writeln;
  writeln('Your array is');
  for i:=1 to sizeB do
    write(b[i],#255);
readln;
end.
