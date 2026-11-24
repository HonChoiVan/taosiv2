# Taosi – Mod Đạo Sĩ (Minecraft Forge 1.20.1)

## Build miễn phí bằng GitHub
1. Tạo repo mới trên GitHub, upload **toàn bộ nội dung** thư mục này (giữ nguyên `.github/workflows/build.yml`).
2. Vào tab **Actions** → workflow *Build Taosi Mod* tự chạy (hoặc bấm *Run workflow*).
3. Chạy xong, mở lần chạy đó → mục **Artifacts** → tải `taosi-mod` → bên trong là file `taosi-1.20.1-1.0.0.jar`.
4. Bỏ file `.jar` vào thư mục `mods` của Minecraft **1.20.1 + Forge 47.2.0 trở lên** (Java 17).

(Muốn build trên máy: cài JDK 17 + Gradle 8.5, chạy `gradle build`.)

## Cách chơi
- Bàn vẽ: **Taosicrafting** = 4 Gỗ Đào (góc) + 4 Sắt (cạnh) + 1 Kim Cương (giữa), craft ở bàn chế tạo thường.
- **Gỗ Đào** = 1 gỗ (bất kỳ log) + 1 thuốc nhuộm hồng → 2 Gỗ Đào.  **Mực Chu Sa** = 1 túi mực + 1 đá đỏ → 2 mực.
- Đặt Taosicrafting xuống và chuột phải để mở giao diện vẽ (công thức chỉ dùng được ở bàn này).
- Vẽ bùa cấp 1: Giấy + Mực + nguyên liệu phụ:
  Nổ=thuốc súng · Tốc độ=đường · Dịch chuyển=ngọc ender · Hoán đổi=ngọc ender+mắt nhện lên men ·
  Làm chậm=slime · Suy yếu=mắt nhện lên men · Tàn hình=thạch anh · Triệu hồi=xương+thịt thối ·
  Sấm sét=thỏi đồng · Kết giới=obsidian.
- Ghép cấp: 2 bùa cùng loại cùng cấp (đặt vào bàn vẽ) → 1 bùa cấp cao hơn (tối đa cấp 3).
- Kiếm Gỗ Đào (vẽ trên bàn): 2 Gỗ Đào + 1 Gậy + 1 Bùa Sấm Sét cấp 1.
  Đánh thường 10 sát thương; chuột phải chém sét 50 sát thương, hồi chiêu 10 giây.

## Bùa
| Bùa | Cấp 1 / 2 / 3 |
|---|---|
| Nổ (ném / đặt K, kích hoạt L) | 20 / 40 / 80 sát thương |
| Sấm sét (ném / đặt K, kích hoạt L) | 25 / 50 / 100 sát thương |
| Tốc độ (chuột phải) | +100% tốc độ chạy, 30s / 60s / 120s |
| Dịch chuyển | 20 / 40 / 80 khối |
| Hoán đổi vị trí với sinh vật đang nhìn | 20 / 40 / 80 khối |
| Làm chậm (ném) | -50% tốc độ, 15s / 30s / 60s |
| Suy yếu (ném) | -30% sát thương, 15s / 30s / 60s |
| Tàn hình | 30s / 60s / 120s |
| Triệu hồi (ném) | 2+2 / 4+4 / 8+8 zombie + skeleton |
| Kết giới 50x50 khối | 30s / 60s / 120s |

Phím mặc định: **K** đặt bùa đang cầm xuống đất (đặt được nhiều lá), **L** kích hoạt tất cả bùa đã đặt cùng lúc. Đổi phím trong Controls → mục "Đạo Sĩ".
