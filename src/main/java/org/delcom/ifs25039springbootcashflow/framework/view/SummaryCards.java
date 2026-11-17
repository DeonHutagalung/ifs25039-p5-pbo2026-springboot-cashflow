package org.delcom.ifs25039springbootcashflow.framework.view;

import org.delcom.ifs25039springbootcashflow.adapter.presenter.FinancePresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/** Kartu ringkasan: total transaksi, pemasukan, pengeluaran, dan saldo. */
public class SummaryCards extends Div {

    private final FinancePresenter presenter;
    private final Span count = value(null);
    private final Span income = value("cf-income");
    private final Span expense = value("cf-expense");
    private final Span balance = value(null);

    public SummaryCards(FinancePresenter presenter) {
        this.presenter = presenter;
        addClassName("cf-summary");
        add(card("Total transaksi", count), card("Total pemasukan", income),
                card("Total pengeluaran", expense), card("Saldo", balance));
    }

    public void update(int transactions, long totalIncome, long totalExpense, long totalBalance) {
        count.setText(String.valueOf(transactions));
        income.setText(presenter.formatMoney(totalIncome));
        expense.setText(presenter.formatMoney(totalExpense));
        balance.setText(presenter.formatMoney(totalBalance));
    }

    private static Span value(String className) {
        Span span = new Span("-");
        span.addClassName("cf-value");
        if (className != null) {
            span.addClassName(className);
        }
        return span;
    }

    private static Div card(String label, Span value) {
        Span caption = new Span(label);
        caption.addClassName("cf-label");
        Div card = new Div(caption, value);
        card.addClassName("cf-card");
        return card;
    }
}
