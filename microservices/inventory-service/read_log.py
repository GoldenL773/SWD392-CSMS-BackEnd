import sys

try:
    with open('d:/WhyFPT/MSS301/exp/SWD392-CSMS-BackEnd/microservices/inventory-service/build_log.txt', 'r', encoding='utf-16') as f:
        print(f.read())
except Exception as e:
    print(f"Error: {e}")
    try:
        with open('d:/WhyFPT/MSS301/exp/SWD392-CSMS-BackEnd/microservices/inventory-service/build_log.txt', 'r', encoding='latin-1') as f:
            print(f.read())
    except:
        print("Fallback failed")
