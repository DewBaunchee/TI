program Project2;
{$APPTYPE CONSOLE}
uses
  SysUtils;

var a: array [1..32767] of integer;
    i, n, s, nZero : integer;
begin
  write ('Enter the number of array elements: ');
  read (n);
  write ('Enter elements of array: ');
  for i := 1 to n do
  read (a[i]);
  writeln ('Thx :)');
  s:=1;
  for i:=1 to n do
  begin
    if a[i]<>0 then
    begin
      nZero:=a[i];
      a[s]:=nZero;
      a[i]:=a[s];
      s:=s+1;
    end;
  end;
  for i:=s to n do
  a[i]:=0;  
  for i:=1 to n do
  write (a[i], ' ');
  readln (n);
end.
