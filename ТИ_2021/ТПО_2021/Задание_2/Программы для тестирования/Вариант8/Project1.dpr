Program Project1;
{$APPTYPE CONSOLE}
uses SysUtils, Math;
Var i,M,N,chislo,a,b: Integer;
Begin
  WriteLn('Vvedite kolichestvo znakov chicla (>=2)');
  ReadLn(M);
  WriteLn('Vvedite delitel');
  ReadLn(N);
  chislo:=round(power(10,M-1));
  While chislo<power(10,M) do begin
                                i:=M;
                                While i>=2 do begin
                                                a:= chislo div round(power(10,i-1)) mod 10;
                                                b:= chislo div round(power(10,i-2)) mod 10;
                                                if a<b then i:=i-1
                                                       else begin
                                                              chislo:=chislo+1;
                                                              if chislo>=power(10,M) then Halt;
                                                              i:=M
                                                            end
                                              end;
                                if chislo mod N =0 then WriteLn('chislo ',chislo,' delitsa na ',N);
                                chislo:=chislo+1;
                              end;
  ReadLn
End.

