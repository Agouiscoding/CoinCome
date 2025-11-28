# import requests
# import pandas as pd
# import time
# import random
# import os
# from datetime import datetime
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
# def fetch_with_retry(url, params, symbol, max_retries=5):
#     for attempt in range(max_retries):
#         try:
#             r = requests.get(url, params=params, timeout=10)
#         except Exception as e:
#             print(f"Network error for {symbol}: {e}")
#             return None
#
#         if r.status_code == 200:
#             return r.json()
#
#         elif r.status_code == 429:
#             wait = 3 * (2 ** attempt) + random.uniform(0, 2)
#             print(f"429 rate limit for {symbol}, waiting {wait:.1f}s (retry {attempt+1}/{max_retries})")
#             time.sleep(wait)
#             continue
#
#         else:
#             print(f"HTTP {r.status_code} for {symbol}, skipping.")
#             return None
#
#     print(f"Failed to fetch {symbol} after retries")
#     return None
#
#
# # -------------------------------------
# # Main loop: fetch last 24h hourly data
# # -------------------------------------
# for coin_id, (cg_id, symbol) in COIN_MAP.items():
#
#     print(f"\nFetching past 24 hours hourly data for {symbol}...")
#
#     url = f"https://api.coingecko.com/api/v3/coins/{cg_id}/market_chart"
#     params = {"vs_currency": "usd", "days": 2}
#
#     data = fetch_with_retry(url, params, symbol)
#     if not data or "prices" not in data:
#         print(f"No data for {symbol}, skip.")
#         continue
#
#     prices = data["prices"]  # list of [timestamp, price]
#
#     folder_path = f"hours/{symbol}"
#     os.makedirs(folder_path, exist_ok=True)
#
#     for timestamp, price in prices:
#         dt = datetime.fromtimestamp(timestamp / 1000)
#
#         row = {
#             "CoinID": coin_id,
#             "Price": price,
#             "IntervalType": "Hour",
#             "HourTime": dt.strftime("%Y-%m-%d %H:00"),
#             "UpdatedAt": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
#         }
#
#         df_row = pd.DataFrame([row])
#
#         filename = f"{folder_path}/{symbol}_{dt.strftime('%Y-%m-%d-%H')}.csv"
#         df_row.to_csv(filename, index=False)
#         print(f"Saved → {filename}")
#
#     time.sleep(random.uniform(1.0, 2.0))
#
# print("\nFinished! You now have full past 24h data.")



import sys
import json
import requests
import pandas as pd
import time
import random
import os
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
from datetime import datetime

try:
    raw_input = sys.stdin.read().strip()
    if raw_input:
        coin_list = json.loads(raw_input)
    else:
        print("❌ No input received from backend.")
        coin_list = []
except Exception as e:
    print("❌ Failed to load JSON:", e)
    coin_list = []

def fetch_with_retry(url, params, symbol, max_retries=5):
    for attempt in range(max_retries):
        try:
            r = requests.get(url, params=params, timeout=10)
        except Exception as e:
            print(f"Network error for {symbol}: {e}")
            return None

        if r.status_code == 200:
            return r.json()

        elif r.status_code == 429:
            wait = 3 * (2 ** attempt) + random.uniform(0, 2)
            print(f"429 rate limit for {symbol}, waiting {wait:.1f}s (retry {attempt+1}/{max_retries})")
            time.sleep(wait)
            continue

        else:
            print(f"HTTP {r.status_code} for {symbol}, skipping.")
            return None

    print(f"Failed to fetch {symbol} after retries")
    return None



# ---------------------------------------------------------
# 新：接收来自后端的参数，而不是写死
# coin_list 示例：
# [
#   {"coin_id": 1, "cg_id": "bitcoin", "symbol": "BTC"},
#   {"coin_id": 2, "cg_id": "ethereum", "symbol": "ETH"},
#   ...
# ]
# ---------------------------------------------------------
def fetch_hourly_data(coin_list):

    for coin in coin_list:
        coin_id = coin["coin_id"]
        cg_id = coin["cg_id"]
        symbol = coin["symbol"]

        print(f"\nFetching past 24 hours hourly data for {symbol}...")

        url = f"https://api.coingecko.com/api/v3/coins/{cg_id}/market_chart"
        params = {"vs_currency": "usd", "days": 2}

        data = fetch_with_retry(url, params, symbol)
        if not data or "prices" not in data:
            print(f"No data for {symbol}, skip.")
            continue

        prices = data["prices"]  # list of [timestamp, price]

        # folder_path = f"hours/{symbol}"
        # os.makedirs(folder_path, exist_ok=True)
        folder_path = os.path.join(BASE_DIR, "hours", symbol)
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
            print(f"Saved → {filename}")

        time.sleep(random.uniform(1.0, 2.0))

    print("\nFinished! You now have full past 24h data.")

if __name__ == "__main__":
    fetch_hourly_data(coin_list)
