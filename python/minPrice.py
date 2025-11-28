import warnings
warnings.filterwarnings("ignore")
import requests, pandas as pd, random, time, os, json, sys
from datetime import datetime

# === 抓实时价格 ===
def fetch_price(cg_id):
    url = "https://api.coingecko.com/api/v3/simple/price"
    params = {"ids": cg_id, "vs_currencies": "usd"}

    while True:
        try:
            r = requests.get(url, params=params, timeout=10)

            if r.status_code == 200:
                data = r.json()
                if cg_id in data:
                    return data[cg_id]["usd"]

            # 429 要等待
            if r.status_code == 429:
                wait = 2 + random.uniform(0, 2)
                time.sleep(wait)
                continue

            # 其它错误，也继续等
            time.sleep(2)

        except:
            time.sleep(random.uniform(1, 2))


# === 主程序：后端传多个 cg_id ===
if __name__ == "__main__":

    # Java 传：python realtime_multi.py bitcoin ethereum solana
    cg_ids = sys.argv[1:]

    if not cg_ids:
        print(json.dumps({"error": "No coin ids provided"}))
        sys.exit(1)

    results = []

    for cg_id in cg_ids:

        # 1. 抓实时价格（一定要抓到）
        price = fetch_price(cg_id)

        # 获取当前时间
        now = datetime.now()
        timestamp = now
        market_date = now.date()
        updated_at = now.strftime("%Y-%m-%d %H:%M:%S")

        # 2. CSV 文件路径（每币一个 CSV）

        BASE_DIR = os.path.dirname(os.path.abspath(__file__))

        # realtime 目录：python/realtime
        folder = os.path.join(BASE_DIR, "realtime")
        os.makedirs(folder, exist_ok=True)

        csv_path = os.path.join(folder, f"{cg_id}.csv")

        # 3. 加载历史 CSV（如果不存在则创建）
        if os.path.isfile(csv_path):
            df = pd.read_csv(csv_path)
        else:
            # 创建一个带表头的 CSV 文件
            df = pd.DataFrame(columns=["timestamp", "Price", "Return", "MarketDate", "UpdatedAt"])
            df.to_csv(csv_path, index=False)  # 创建文件

        # 4. 新增一行
        new_row = {
            "timestamp": timestamp,
            "Price": price,
            "MarketDate": market_date,
            "UpdatedAt": updated_at,
        }

        df = pd.concat([df, pd.DataFrame([new_row])], ignore_index=True)

        # 补充收益率
        df["Return"] = df["Price"].pct_change()

        # 5. 保存 CSV
        df.to_csv(csv_path, index=False)

        # 6. 输出给 Java
        latest = df.iloc[-1].to_dict()
        latest["cg_id"] = cg_id
        results.append(latest)

    # 7. 最终 JSON 输出（后端直接接）
    print(json.dumps(results, default=str))
