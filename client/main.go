package main

import (
	"context"
	"fmt"
	"os"

	tea "github.com/charmbracelet/bubbletea"
	"github.com/charmbracelet/lipgloss"
)

var (
	titleStyle = lipgloss.NewStyle().
			BorderStyle(lipgloss.ASCIIBorder()).
			Padding(4).
			Bold(true).
			Foreground(lipgloss.Color("#7D56F4"))

	counterStyle = lipgloss.NewStyle().
			Foreground(lipgloss.Color("#00FFAA")).
			Padding(1, 2).
			Border(lipgloss.NormalBorder()).
			BorderForeground(lipgloss.Color("#FF00AA"))

	dashBoardStyle = lipgloss.NewStyle().
			Foreground(lipgloss.Color("#00FFBB")).
			Padding(2, 3).
			Background(lipgloss.Color("#00CCDD")).
			BorderForeground(lipgloss.Color("#00FFEE"))
)

type model struct {
	count int
}

func main() {
	p := tea.NewProgram(model{}, tea.WithAltScreen(), tea.WithFPS(60), tea.WithContext(context.Background()))
	if _, err := p.Run(); err != nil {
		fmt.Printf("There's been an error: %v", err)
		os.Exit(1)
	}
}

func (m model) Init() tea.Cmd {
	// Just return `nil`, which means "no I/O right now, please."
	return nil
}

func (m model) Update(msg tea.Msg) (tea.Model, tea.Cmd) {
	tea.ClearScreen()
	switch msg := msg.(type) {

	case tea.KeyMsg:
		switch msg.String() {
		case "q", "ctrl+c":
			return m, tea.Quit
		case "up":
			m.count++
		case "down":
			m.count--
		case "right":
			m.count++
		case "left":
			m.count--
		default:
			return m, tea.Quit
		}
	}
	return m, nil

}

func (m model) View() string {
	title := titleStyle.Render("🧮 Counter App")
	counter := counterStyle.Render(fmt.Sprintf("%d", m.count))

	help := "\n[↑] increase | [↓] decrease | [q] quit"
	return fmt.Sprintf("%s\n\n%s\n%s", title, counter, help)

}
