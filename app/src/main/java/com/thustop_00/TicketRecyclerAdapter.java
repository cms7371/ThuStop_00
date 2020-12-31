package com.thustop_00;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thustop_00.model.Ticket;
import com.thustop_00.widgets.NotoTextView;

import java.util.ArrayList;


public class TicketRecyclerAdapter extends RecyclerView.Adapter<TicketRecyclerAdapter.TicketViewHolder> {
    private ArrayList<Ticket> tickets;
    private Context context;
    private boolean isClickable;

    public interface TicketRecyclerListener {
        void onItemSelected(View view, int position);
    }

    private TicketRecyclerListener listener;

    public void setListener(TicketRecyclerListener listener) {
        this.listener = listener;
    }

    TicketRecyclerAdapter(Context context, ArrayList<Ticket> in, boolean isClickable) {
        this.context = context;
        this.tickets = in;
        this.isClickable = isClickable;
    }

    @Override
    public void onBindViewHolder(@NonNull TicketRecyclerAdapter.TicketViewHolder holder, int position) {
        //TODO 위치에 따라 ticket 클래스 값 할당, 리스너 등록
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder {
        protected NotoTextView busName;
        protected NotoTextView departureTime;
        protected NotoTextView departure;
        protected NotoTextView destination;
        protected NotoTextView destinationTime;
        protected NotoTextView capacity;
        protected NotoTextView ticketState;
        protected ProgressBar pb_capacity;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            this.busName = (NotoTextView) itemView.findViewById(R.id.tv_ticket_bus_name);
            this.departure = (NotoTextView) itemView.findViewById(R.id.tv_departure);
            this.departureTime = (NotoTextView) itemView.findViewById(R.id.tv_departure_time);
            this.destination = (NotoTextView) itemView.findViewById(R.id.tv_destination);
            this.destinationTime = (NotoTextView) itemView.findViewById(R.id.tv_destination_time);
            this.capacity = (NotoTextView) itemView.findViewById(R.id.tv_capacity);
            this.ticketState = (NotoTextView) itemView.findViewById(R.id.tv_ticket_state);
            this.pb_capacity = (ProgressBar) itemView.findViewById(R.id.pb_personnel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO 테스트를 위해 바꿔놓은거 원래대로
                    if (listener != null) {
                        listener.onItemSelected(view, getAdapterPosition());
                        //listener.onItemSelected(view,  getAdapterPosition());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public TicketRecyclerAdapter.TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        if (!isClickable)
            itemView.setBackground(null);
        return new TicketRecyclerAdapter.TicketViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return 3; //TODO 티켓 갯수에 따라 길이 바꿔주도록 해야합니다.
    }

}

