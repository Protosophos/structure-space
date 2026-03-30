"""
Interactive Mathematical Function Plotter - similar to Desmos.
Uses matplotlib and numpy to plot mathematical functions.

Usage:
    python funktionsplotter.py
"""

import numpy as np
import matplotlib.pyplot as plt
from matplotlib.widgets import CheckButtons, Slider

# ---------------------------------------------------------------------------
# Define x range
# ---------------------------------------------------------------------------
x = np.linspace(-10, 10, 1000)

# ---------------------------------------------------------------------------
# Define mathematical functions
# ---------------------------------------------------------------------------
functions = {
    "Linear: f(x) = 2x + 1": lambda x: 2 * x + 1,
    "Quadratic: f(x) = x^2": lambda x: x ** 2,
    "Cubic: f(x) = x^3": lambda x: x ** 3,
    "Exponential: f(x) = e^x": lambda x: np.exp(x),
    "Exponential: f(x) = 2^x": lambda x: 2 ** x,
    "Logarithmic: f(x) = ln(x)": lambda x: np.log(np.where(x > 0, x, np.nan)),
    "Square root: f(x) = sqrt(x)": lambda x: np.sqrt(np.where(x >= 0, x, np.nan)),
    "Sine: f(x) = sin(x)": lambda x: np.sin(x),
    "Cosine: f(x) = cos(x)": lambda x: np.cos(x),
    "Tangent: f(x) = tan(x)": lambda x: np.where(
        np.abs(np.cos(x)) > 0.01, np.tan(x), np.nan
    ),
    "Absolute: f(x) = |x|": lambda x: np.abs(x),
    "1/x (Hyperbola)": lambda x: np.where(np.abs(x) > 0.01, 1 / x, np.nan),
}

# Colors for each function
colors = [
    "#e74c3c", "#3498db", "#2ecc71", "#f39c12", "#9b59b6", "#1abc9c",
    "#e67e22", "#2980b9", "#27ae60", "#8e44ad", "#d35400", "#16a085",
]

# ---------------------------------------------------------------------------
# Create the plot
# ---------------------------------------------------------------------------
fig, ax = plt.subplots(figsize=(12, 8))
fig.subplots_adjust(left=0.30, bottom=0.15)
fig.patch.set_facecolor("#1e1e2e")
ax.set_facecolor("#1e1e2e")

# Style the axes
ax.spines["left"].set_color("white")
ax.spines["bottom"].set_color("white")
ax.spines["top"].set_visible(False)
ax.spines["right"].set_visible(False)
ax.tick_params(colors="white")
ax.xaxis.label.set_color("white")
ax.yaxis.label.set_color("white")

# Draw coordinate axes through the origin
ax.axhline(y=0, color="gray", linewidth=0.5, linestyle="-")
ax.axvline(x=0, color="gray", linewidth=0.5, linestyle="-")

# Grid
ax.grid(True, alpha=0.2, color="white")
ax.set_xlim(-10, 10)
ax.set_ylim(-10, 10)
ax.set_xlabel("x", fontsize=12)
ax.set_ylabel("f(x)", fontsize=12)
ax.set_title("Function Plotter", fontsize=16, color="white", fontweight="bold")

# Plot all functions (initially only first 3 visible)
lines = {}
labels = list(functions.keys())
initial_visible = [True, True, True] + [False] * (len(functions) - 3)

for i, (label, func) in enumerate(functions.items()):
    y = func(x)
    (line,) = ax.plot(x, y, color=colors[i], linewidth=2, label=label)
    line.set_visible(initial_visible[i])
    lines[label] = line

# ---------------------------------------------------------------------------
# Checkbox widget to toggle functions
# ---------------------------------------------------------------------------
checkbox_ax = fig.add_axes([0.01, 0.15, 0.24, 0.75])
checkbox_ax.set_facecolor("#2d2d44")
checkbox_ax.set_frame_on(True)

check = CheckButtons(
    checkbox_ax,
    labels,
    initial_visible,
)

# Style checkbox labels
for text, color in zip(check.labels, colors):
    text.set_color(color)
    text.set_fontsize(9)


def toggle_function(label):
    lines[label].set_visible(not lines[label].get_visible())
    fig.canvas.draw_idle()


check.on_clicked(toggle_function)

# ---------------------------------------------------------------------------
# Zoom slider
# ---------------------------------------------------------------------------
slider_ax = fig.add_axes([0.30, 0.03, 0.60, 0.03])
slider_ax.set_facecolor("#2d2d44")
zoom_slider = Slider(slider_ax, "Zoom", 1, 50, valinit=10, color="#3498db")
zoom_slider.label.set_color("white")
zoom_slider.valtext.set_color("white")


def update_zoom(val):
    limit = zoom_slider.val
    ax.set_xlim(-limit, limit)
    ax.set_ylim(-limit, limit)
    fig.canvas.draw_idle()


zoom_slider.on_changed(update_zoom)

# ---------------------------------------------------------------------------
# Show
# ---------------------------------------------------------------------------
plt.show()
