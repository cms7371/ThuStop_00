package com.thustop_00;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.thustop_00.databinding.FragmentNavPersonalHistoryTicketBinding;
import com.thustop_00.model.Ticket;
import com.thustop_00.widgets.NotoTextView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavPersonalHistoryTicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavPersonalHistoryTicketFragment extends FragmentBase {

    FragmentNavPersonalHistoryTicketBinding binding;
    private NavTicketAdapter mAdapter;
    private ArrayList<Ticket> mArrayList;

    public NavPersonalHistoryTicketFragment() {
        // Required empty public constructor
    }


    public static NavPersonalHistoryTicketFragment newInstance(String param1, String param2) {
        NavPersonalHistoryTicketFragment fragment = new NavPersonalHistoryTicketFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentNavPersonalHistoryTicketBinding.inflate(inflater);
        binding.setNavPersonalHistoryTicketFrag(this);
        mArrayList = new ArrayList<>();
        mAdapter = new NavTicketAdapter(mArrayList);

        binding.rvTicket.setAdapter(mAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.rvTicket);

        SnapPagerListener mListener = new SnapPagerListener(pagerSnapHelper, SnapPagerListener.ON_SCROLL, true, new SnapPagerListener.OnChangeListener() {
            @Override
            public void onSnapped(int position) {
            }
        });

        binding.rvTicket.addOnScrollListener(mListener);

        setIndicator(binding.rvTicket.getAdapter().getItemCount());
        return binding.getRoot();
    }

    public void setIndicator(int numTicket) {
        if (numTicket > 1) {
            //binding.rvTicket.addItemDecoration(new RecyclerIndicator());
            binding.rvTicket.addItemDecoration(new RecyclerIndicator(R.color.Ach97, R.color.AchCF, 32, 6, 10));
        } else {
            binding.rvTicket.setPadding(0,0,0,0);
        }
    }
    public void onTicketPointClick(View view) {
        _listener.addFragment(NavPersonalHistoryTicketPointFragment.newInstance());
    }
    public void onTransformInfoClick(View view) {
        Log.d("ㅇㅇ","눌렸음");
        NoticeTicketPointDialog noticeTicketPointDialog = new NoticeTicketPointDialog(getContext());
        noticeTicketPointDialog.show();
    }

    private class NavTicketAdapter extends RecyclerView.Adapter<NavTicketAdapter.TicketViewHolder> {
        private ArrayList<Ticket> tickets;
        NavTicketAdapter(ArrayList<Ticket> in) {
            this.tickets = in;
        }


        @NonNull
        @Override
        public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_ticket, parent, false);
            TicketViewHolder viewHolder = new TicketViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 4;
        }

        public class TicketViewHolder extends RecyclerView.ViewHolder {
            protected NotoTextView busCode;
            protected NotoTextView departureTime;
            protected NotoTextView departure;
            protected NotoTextView destination;
            protected NotoTextView destinationTime;
            protected NotoTextView capacity;
            protected NotoTextView ticketState;
            protected ProgressBar pb_capacity;


            public TicketViewHolder(@NonNull View itemView) {
                super(itemView);
                this.busCode = (NotoTextView) itemView.findViewById(R.id.tv_ticket_bus_code);
                this.departure = (NotoTextView) itemView.findViewById(R.id.tv_departure);
                this.departureTime = (NotoTextView) itemView.findViewById(R.id.tv_departure_time);
                this.destination = (NotoTextView) itemView.findViewById(R.id.tv_destination);
                this.destinationTime = (NotoTextView) itemView.findViewById(R.id.tv_destination_time);
                this.capacity = (NotoTextView) itemView.findViewById(R.id.tv_capacity);
                this.ticketState = (NotoTextView) itemView.findViewById(R.id.tv_ticket_state);
                this.pb_capacity = (ProgressBar) itemView.findViewById(R.id.pb_personnel);

            }
        }
    }

    public static class SnapPagerListener extends RecyclerView.OnScrollListener {
        public static final int ON_SCROLL = 0;
        public static final int ON_SETTLED = 1;

        private PagerSnapHelper snapHelper;
        private int type;
        private boolean notifyOnInit;
        private OnChangeListener listener;
        private int snapPosition;

        @IntDef({ON_SCROLL, ON_SETTLED})
        public @interface Type {
        }

        public SnapPagerListener(PagerSnapHelper snapHelper, int type, boolean notifyOnInit, OnChangeListener listener) {
            this.snapHelper = snapHelper;
            this.type = type;
            this.notifyOnInit = notifyOnInit;
            this.listener = listener;
            this.snapPosition = RecyclerView.NO_POSITION;
        }

        public interface OnChangeListener {
            void onSnapped(int position);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if ((type == ON_SCROLL)|| !hasItemPosition()) {
                notifyListenerIfNeeded(getSnapPosition(recyclerView));
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (type == ON_SETTLED && newState == RecyclerView.SCROLL_STATE_IDLE) {
                notifyListenerIfNeeded(getSnapPosition(recyclerView));
            }
        }

        private int getSnapPosition(RecyclerView recyclerView) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager == null) {
                return RecyclerView.NO_POSITION;
            }

            View snapView = snapHelper.findSnapView(layoutManager);
            if (snapView == null) {
                return RecyclerView.NO_POSITION;
            }

            return layoutManager.getPosition(snapView);
        }

        private void notifyListenerIfNeeded(int newSnapPosition) {
            if (snapPosition != newSnapPosition) {
                if (notifyOnInit && !hasItemPosition()) {
                    listener.onSnapped(newSnapPosition);
                } else if (hasItemPosition()) {
                    listener.onSnapped(newSnapPosition);

                }

                snapPosition = newSnapPosition;
            }
        }
        private boolean hasItemPosition() {
            return snapPosition != RecyclerView.NO_POSITION;
        }


    }


}