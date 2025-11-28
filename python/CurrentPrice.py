import requests
import json
import sys
import time
import random

def fetch_price(cg_id):
    url = "https://api.coingecko.com/api/v3/simple/price"
    params = {"ids": cg_id, "vs_currencies": "usd"}

    while True:
        try:
            r = requests.get(url, params=params, timeout=10)

            # 成功
            if r.status_code == 200:
                data = r.json()

                # 找到价格
                if cg_id in data:
                    return data[cg_id]["usd"]

            # 429：访问太频繁 → 等待后重试
            if r.status_code == 429:
                wait = 2 + random.uniform(0, 2)
                time.sleep(wait)
                continue

            # 其它错误 → 稍等重试
            time.sleep(2)

        except Exception:
            # 网络错误 → 随机等待 (避免造成频繁请求)
            time.sleep(random.uniform(1.0, 2.0))


if __name__ == "__main__":
    cg_id = sys.argv[1]
    price = fetch_price(cg_id)

    print(json.dumps({"price": price}))


