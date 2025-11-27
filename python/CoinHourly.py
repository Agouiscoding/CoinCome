# # 每小时抓一次价格，每小时一个 CSV 文件（带重试）
#
# import requests, pandas as pd, time, random
# from datetime import datetime
# import os
#
# COIN_MAP = {
#     1: ("bitcoin", "BTC"),
#     2: ("ethereum", "ETH"),
#     3: ("tether", "USDT"),
#     4: ("solana", "SOL"),
#     5: ("cardano", "ADA"),
#     6: ("ripple", "XRP"),
#     7: ("dogecoin", "DOGE"),
#     8: ("binancecoin", "BNB"),
#     9: ("litecoin", "LTC"),
#     10: ("avalanche-2", "AVAX")
# }
#
# def fetch_with_retry(cg_id, symbol, max_retries=5):
#     url = "https://api.coingecko.com/api/v3/simple/price"
#     params = {
#         "ids": cg_id,
#         "vs_currencies": "usd"
#     }
#
#     for attempt in range(max_retries):
#         try:
#             r = requests.get(url, params=params, timeout=10)
#         except Exception as e:
#             print(f"❌ Network error for {symbol}: {e}")
#             return None
#
#         if r.status_code == 200:
#             return r.json()
#
#         elif r.status_code == 429:
#             wait = 3 * (2 ** attempt) + random.uniform(0, 2)
#             print(f"⚠️ 429 for {symbol}, waiting {wait:.1f}s (retry {attempt+1}/{max_retries})")
#             time.sleep(wait)
#             continue
#
#         else:
#             print(f"⚠️ HTTP {r.status_code} for {symbol}")
#             return None
#
#     print(f"❌ Failed after {max_retries} retries for {symbol}")
#     return None
#
#
# for coin_id, (cg_id, symbol) in COIN_MAP.items():
#
#     print(f"\n📊 Fetching hourly price for {symbol}...")
#
#     data = fetch_with_retry(cg_id, symbol)
#     if not data or cg_id not in data:
#         print(f"⚠️ No price data for {symbol}, skipping.")
#         continue
#
#     price = data[cg_id]["usd"]
#     now = datetime.now()
#
#     row = {
#         "CoinID": coin_id,
#         "Price": price,
#         "IntervalType": "Hour",
#         "HourTime": now.strftime("%Y-%m-%d %H:00"),
#         "UpdatedAt": now.strftime("%Y-%m-%d %H:%M:%S")
#     }
#
#     df_row = pd.DataFrame([row])
#
#     # 每小时一个文件
#     folder_path = f"hours/{symbol}"
#     os.makedirs(folder_path, exist_ok=True)
#
#     filename = f"{folder_path}/{symbol}_{now.strftime('%Y-%m-%d-%H')}.csv"
#
#     df_row.to_csv(filename, index=False)
#
#     print(f"✅ Saved → {filename}")
#
#     time.sleep(random.uniform(1.0, 2.0))
#
# print("\n🎉 All done!")


# 一次性抓取过去 24 小时数据（每小时一条），每小时一个 CSV 文件

import requests, pandas as pd, time, random
from datetime import datetime
import os

COIN_MAP = {
    1: ("bitcoin", "BTC"),
    2: ("ethereum", "ETH"),
    3: ("tether", "USDT"),
    4: ("solana", "SOL"),
    5: ("cardano", "ADA"),
    6: ("ripple", "XRP"),
    7: ("dogecoin", "DOGE"),
    8: ("binancecoin", "BNB"),
    9: ("litecoin", "LTC"),
    10: ("avalanche-2", "AVAX")
}

def fetch_with_retry(url, params, symbol, max_retries=5):
    for attempt in range(max_retries):
        try:
            r = requests.get(url, params=params, timeout=10)
        except Exception as e:
            print(f"❌ Network error for {symbol}: {e}")
            return None

        if r.status_code == 200:
            return r.json()

        elif r.status_code == 429:
            wait = 3 * (2 ** attempt) + random.uniform(0, 2)
            print(f"⚠️ 429 for {symbol}, waiting {wait:.1f}s (retry {attempt+1}/{max_retries})")
            time.sleep(wait)
            continue

        else:
            print(f"⚠️ HTTP {r.status_code} for {symbol}, skipping.")
            return None

    print(f"❌ Failed to fetch {symbol} after retries")
    return None


for coin_id, (cg_id, symbol) in COIN_MAP.items():

    print(f"\n📊 Fetching past 24 hours hourly data for {symbol}...")

    url = f"https://api.coingecko.com/api/v3/coins/{cg_id}/market_chart"
    params = {"vs_currency": "usd", "days": 1}

    data = fetch_with_retry(url, params, symbol)
    if not data or "prices" not in data:
        print(f"⚠️ No data for {symbol}, skip.")
        continue

    prices = data["prices"]  # 每小时一条: [timestamp, price]

    folder_path = f"hours/{symbol}"
    os.makedirs(folder_path, exist_ok=True)

    for timestamp, price in prices:
        dt = datetime.fromtimestamp(timestamp / 1000)

        row = {
            "CoinID": coin_id,
            "Price": price,
            "IntervalType": "Hour",
            "HourTime": dt.strftime("%Y-%m-%d %H:00"),
            "UpdatedAt": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }

        df_row = pd.DataFrame([row])

        filename = f"{folder_path}/{symbol}_{dt.strftime('%Y-%m-%d-%H')}.csv"

        df_row.to_csv(filename, index=False)
        print(f"✅ Saved → {filename}")

    time.sleep(random.uniform(1.0, 2.0))

print("\n🎉 Finished! You now have full past 24h data ❤️")

