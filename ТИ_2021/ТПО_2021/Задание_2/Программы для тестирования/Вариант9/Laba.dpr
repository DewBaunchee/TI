program Project2;

{$APPTYPE CONSOLE}

uses
  SysUtils;

var i,M,n, prov:integer;
    e:string;
begin
  repeat
    Write('Please, enter M:');
    ReadLn(M);
    M:=Abs(M);            //Модуль М для отрицательных чисел
    i:=2;
    prov:=0;              //Проверка на простые числа
    n:= (M div 2)+1;
    while(i<n) do         //Поиск делителей
    begin
      if(m mod i=0)
        then
        begin
          M:=M div i;                                                                               
          WriteLn(i);
          prov:=1;
        end
        else inc(i);
    end;
    if(prov =0)
      then
        begin
          if (M=1) or (M=0)
            then Writeln('Cannot be dicomposed')        //"Нельзя разложить"
            else Writeln(M);       //"Число простое"
      end;
    Write('If you want to close this program, write "exit":');ReadLn(e);
  until e='exit';                                                          //Выход из программы
end.
