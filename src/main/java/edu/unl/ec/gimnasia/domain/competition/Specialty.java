package edu.unl.ec.gimnasia.domain.competition;

public enum Specialty {

    DIFICULTAD(false, new String[]{
            "Saltos", "Giros", "Equilibrios", "Ondas",
            "Combinaciones", "Riesgo de los elementos", "Preacrobáticos",
            "Variedad de movimientos", "Originalidad", "Conexión entre elementos"
    }),
    EJECUCION(true, new String[]{
            "Caídas", "Postura", "Precisión", "Control del aparato",
            "Sincronización", "Flexibilidad", "Coordinación corporal",
            "Estabilidad", "Limpieza técnica", "Fluidez"
    }),
    ARTISTICO(true, new String[]{
            "Expresión corporal", "Expresión facial", "Musicalidad", "Coreografía",
            "Uso del espacio", "Interpretación musical", "Creatividad",
            "Presencia escénica", "Armonía", "Conexión con el público"
    });

    private static final double BASE_SCORE = 10.0;

    private final boolean penalty;
    private final String[] criteria;

    Specialty(boolean penalty, String[] criteria) {
        this.penalty = penalty;
        this.criteria = criteria;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public String[] getCriteria() {
        return criteria.clone();
    }

    public double calculateTotal(double sumOfValues) {
        if (!penalty) {
            return sumOfValues;
        }
        return Math.max(0.0, BASE_SCORE - sumOfValues);
    }
}
