program Laba1_Pliska_651005_korotkaya;

{$APPTYPE CONSOLE}

 var Q,P,i,k:integer;
 var C: boolean;
begin
    readln(Q,P);
      for i:=2 to Q do
      if Q mod i=0 then
      begin
        C:=false;k:=2;
        while k<=i do begin
              if (i mod k=0) and (P mod k=0) then C:=true;
              k:=k+1;
              end;
            if C=false then write(i,' ');

      END;
    readln;

end.
 