import os
import pandas as pd
import matplotlib.pyplot as plt

os.makedirs('results/plots', exist_ok=True)
csv_file = 'results/results.csv'

if not os.path.exists(csv_file):
    print(f"Error: {csv_file} not found. Please run Benchmark.java first.")
    exit(1)

df = pd.read_csv(csv_file)

workloads = df['workload'].unique()

for wl in workloads:
    sub = df[df['workload'] == wl]
    variants = sub['variant'].unique()

    for var in variants:
        var_sub = sub[sub['variant'] == var]
        title_suffix = f" (variant={var})" if var != "-" else ""
        file_suffix = f"_{var}" if var != "-" else ""

        # 1. Execution Time vs N
        plt.figure(figsize=(8, 5))
        for ds in var_sub['structure'].unique():
            ds_data = var_sub[var_sub['structure'] == ds]
            plt.plot(ds_data['n'], ds_data['time_ms'], marker='o', label=ds)

        plt.title(f'Workload: {wl}{title_suffix} — Execution Time vs N')
        plt.xlabel('N (Number of Elements)')
        plt.ylabel('Time (ms)')
        plt.xscale('log')
        plt.grid(True, which="both", linestyle='--', alpha=0.6)
        plt.legend()
        plt.tight_layout()
        plt.savefig(f'results/plots/{wl}{file_suffix}_time.png')
        plt.close()

        # 2. Steps / Moves / Comparisons vs N
        plt.figure(figsize=(8, 5))
        for ds in var_sub['structure'].unique():
            ds_data = var_sub[var_sub['structure'] == ds]
            plt.plot(ds_data['n'], ds_data['steps'], marker='o', linestyle='-', label=f'{ds} (steps)')
            if ds_data['moves'].sum() > 0:
                plt.plot(ds_data['n'], ds_data['moves'], marker='s', linestyle='--', label=f'{ds} (moves)')
            if ds_data['comparisons'].sum() > 0:
                plt.plot(ds_data['n'], ds_data['comparisons'], marker='^', linestyle=':', label=f'{ds} (comparisons)')

        plt.title(f'Workload: {wl}{title_suffix} — Operations vs N')
        plt.xlabel('N (Number of Elements)')
        plt.ylabel('Count')
        plt.xscale('log')
        plt.yscale('log')
        plt.grid(True, which="both", linestyle='--', alpha=0.6)
        plt.legend()
        plt.tight_layout()
        plt.savefig(f'results/plots/{wl}{file_suffix}_metrics.png')
        plt.close()

print("All plots generated successfully in results/plots/")