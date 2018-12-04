package hk.ust.gmission.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.Attachment;
import hk.ust.gmission.models.Selection;
import hk.ust.gmission.services.AnswerService;
import hk.ust.gmission.services.AttachmentService;
import hk.ust.gmission.services.HitService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bigstone on 6/1/2016.
 */
public class AnswerRecyclerViewAdapter extends BaseRecyclerViewAdapter<AnswerRecyclerViewAdapter.ViewHolder, Answer>{

    private HitService hitService;
    private AttachmentService attachmentService;
    private AnswerService answerService;
    private Context context;

    private boolean isViewOnly;

    public void setViewOnly(boolean viewOnly) {
        isViewOnly = viewOnly;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_item, parent, false);

        return new ViewHolder(itemView);
    }

    public AnswerRecyclerViewAdapter(Context context, HitService hitService, AttachmentService attachmentService, AnswerService answerService) {
        items = new ArrayList<>();
        Injector.inject(this);

        this.context = context;
        this.hitService = hitService;
        this.attachmentService = attachmentService;
        this.answerService = answerService;
    }

    public int pxFromDp(final float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Answer answer = items.get(position);

        holder.answeredTime.setText(answer.getCreated_on().toLocaleString());


        if (answer.getType().equals(Constants.Extra.IMAGE_TYPE) || answer.getType().equals(Constants.Extra.MODEL_IMAGE_TYPE)){

            holder.frameLayout.getLayoutParams().height = pxFromDp(450);//pixels
            holder.textAnswer.setVisibility(View.GONE);
            holder.imageAnswer.setVisibility(View.VISIBLE);

            attachmentService.getAttachment(answer.getAttachment_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Attachment>() {
                        @Override
                        public void call(Attachment attachment) {
                            Picasso.with(holder.imageAnswer.getContext())
                                    .load(Constants.Http.URL_IMAGE_ORI + "/" + attachment.getValue())
                                    .centerCrop()
                                    .resize(holder.imageAnswer.getMeasuredWidth(), holder.imageAnswer.getMeasuredHeight())
                                    .into(holder.imageAnswer);
                        }
                    }).subscribe();
        } else {
//            holder.frameLayout.getLayoutParams().height = pxFromDp(100);//pixels
            holder.textAnswer.setVisibility(View.VISIBLE);
            holder.imageAnswer.setVisibility(View.GONE);
        }

        if (answer.getType().equals(Constants.Extra.TEXT_TYPE)){
            holder.textAnswer.setText(String.format("%s", answer.getBrief()));
        }

        if (answer.getType().equals(Constants.Extra.CHOICE_TYPE)){
            hitService.getSelection(answer.getBrief())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Selection>() {
                        @Override
                        public void call(Selection selection) {
                            holder.textAnswer.setText(String.format("%s", selection.getBrief()));
                        }
                    }).subscribe();
        }

        if (answer.isAccepted() == false) {
            holder.approvedIcon.setVisibility(View.INVISIBLE);
        }

        if (answer.isAccepted() == true) {
            holder.approvedIcon.setVisibility(View.VISIBLE);
        }

    }




    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.frame_layout) FrameLayout frameLayout;
        @Bind(R.id.approved_ic) ImageView approvedIcon;
        @Bind(R.id.text_answer) TextView textAnswer;
        @Bind(R.id.image_answer) ImageView imageAnswer;
        @Bind(R.id.answered_time) TextView answeredTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (isViewOnly) {
                return;
            }

            final Answer answer = getItem(getAdapterPosition());
            Boolean hasApproved = false;

            if (approvedIcon.getVisibility() == View.VISIBLE) {
                hasApproved = true;
            }

            if (approvedIcon.getVisibility() == View.INVISIBLE || approvedIcon.getVisibility() == View.GONE){
                hasApproved = false;
            }

            Observable.just(hasApproved)
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<Boolean, Observable<Answer>>() {
                        @Override
                        public Observable<Answer> call(Boolean hasApproved) {
                            if (!hasApproved) {
                                answer.setAccepted(true);
                            }

                            if (hasApproved) {
                                answer.setAccepted(false);
                            }

                            return answerService.updateAnswer(answer.getId(), answer);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Answer>() {
                        @Override
                        public void call(Answer answer) {

                            if (answer.isAccepted()) {
                                approvedIcon.setVisibility(View.VISIBLE);
                            } else {
                                approvedIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).subscribe();


        }
    }
}
