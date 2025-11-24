import requests, pandas as pd, time, random
from datetime import datetime

COIN_MAP = {
    # 1: ("bitcoin", "BTC"),
    2: ("ethereum", "ETH"),
    # 3: ("tether", "USDT"),
    # 4: ("solana", "SOL"),
    # 5: ("cardano", "ADA"),
    # 6: ("ripple", "XRP"),
    # 7: ("dogecoin", "DOGE"),
    # 8: ("binancecoin", "BNB"),
    # 9: ("litecoin", "LTC"),
    # 10: ("avalanche-2", "AVAX")
}

DAYS = 30
frames = []

def fetch_with_retry(url, params, symbol, max_retries=5):
    """处理 HTTP429 的智能重试"""
    for attempt in range(max_retries):
        try:
            r = requests.get(url, params=params, timeout=10)
        except Exception as e:
            print(f"❌ Network error for {symbol}: {e}")
            return None

        if r.status_code == 200:
            return r

        elif r.status_code == 429:
            wait = 5 * (2 ** attempt) + random.uniform(0, 3)
            print(f"⚠️ HTTP 429 for {symbol}, waiting {wait:.1f}s before retry ({attempt+1}/{max_retries})...")
            time.sleep(wait)
            continue

        else:
            print(f"⚠️ HTTP {r.status_code} for {symbol}, skipping.")
            return None

    print(f"❌ Failed to fetch {symbol} after {max_retries} retries.")
    return None


for coin_id, (cg_id, symbol) in COIN_MAP.items():
    print(f"📊 Fetching {symbol} data...")
    url = f"https://api.coingecko.com/api/v3/coins/{cg_id}/market_chart"
    params = {"vs_currency": "usd", "days": DAYS}

    r = fetch_with_retry(url, params, symbol)
    #print(cg_id)

    if not r:
        continue


    try:
        data = r.json()
    except Exception as e:
        print(f"⚠️ JSON decode failed for {symbol}: {e}")
        continue

    prices = data.get("prices", [])
    if not prices:
        print(f"⚠️ No price data for {symbol}, skipping.")
        continue

    df = pd.DataFrame(prices, columns=["timestamp", "Price"])
    df["timestamp"] = pd.to_datetime(df["timestamp"], unit="ms")
    df = df.resample("1D", on="timestamp").last().reset_index()
    df["CoinID"] = coin_id
    df["Return"] = df["Price"].pct_change()
    df["IntervalType"] = "daily"
    df["MarketDate"] = df["timestamp"].dt.date
    df["UpdatedAt"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    df = df.dropna(subset=["Return"])
    frames.append(df[["CoinID", "Price", "Return", "IntervalType", "MarketDate", "UpdatedAt"]])

    # 额外加点随机延迟（1~3秒），更像人类访问
    time.sleep(random.uniform(1.0, 3.0))

# === 合并并保存 ===
if frames:
    result = pd.concat(frames, ignore_index=True)
    filename = f"marketdata_mysql_{datetime.now().strftime('%Y%m%d_%H%M')}.csv"
    result.to_csv(filename, index=False)
    print(f"\n✅ Done! Saved to {filename}")
    print(result.head())
else:
    print("⚠️ No data fetched at all.")