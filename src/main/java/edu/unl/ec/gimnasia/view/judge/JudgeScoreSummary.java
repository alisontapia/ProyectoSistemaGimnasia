package edu.unl.ec.gimnasia.view.judge;

import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.evaluation.Evaluation;
import edu.unl.ec.gimnasia.domain.people.Gymnast;

import java.io.Serializable;
import java.util.List;

public class JudgeScoreSummary implements Serializable {

    private final Competition competition;
    private final Category category;
    private final Gymnast gymnast;
    private final Specialty specialty;
    private final double finalScore;
    private final List<Evaluation> details;

    public JudgeScoreSummary(Competition competition, Category category, Gymnast gymnast,
                              Specialty specialty, double finalScore, List<Evaluation> details) {
        this.competition = competition;
        this.category = category;
        this.gymnast = gymnast;
        this.specialty = specialty;
        this.finalScore = finalScore;
        this.details = details;
    }

    public boolean isClosed() {
        return competition != null && competition.isClosed();
    }

    public String getEstado() {
        return isClosed() ? "Cerrada (solo consulta)" : "Abierta (editable)";
    }

    public double getTotalPenalizaciones() {return details == null ? 0.0 : details.stream().mapToDouble(Evaluation::getScore).sum();
    }
    public Competition getCompetition() {
        return competition;
    }

    public Category getCategory() {
        return category;
    }

    public Gymnast getGymnast() {
        return gymnast;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public List<Evaluation> getDetails() {
        return details;
    }
}
