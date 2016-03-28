/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package leo.com.lazyloadfragment.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import leo.com.lazyloadfragment.R;


/**
 * pstsindicatorcolor滑动指示器颜色 pstsunderlinecolor在视图的底部的全宽度的线颜色
 * pstsdividercolor选项卡之间的分隔颜色 滑动指示器pstsindicatorheightheight
 * 在视图的底部的全宽度的线pstsunderlineheight高度 pstsdividerpadding顶部和底部填充的分频器
 * pststabpaddingleftright左、右填充每个选项卡 pstsscrolloffset卷轴被选择的标签的偏移
 * pststabbackground背景绘制的每个标签，应该是一个statelistdrawable
 * pstsshouldexpand如果设置为TRUE，每个标签都给予同样的重量，默认为false
 * pststextallcaps如果为真，所有选项卡标题都是大写，默认为true
 *
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {

	// @formatter:off
	private static final int[] ATTRS = new int[] { android.R.attr.textSize,
			android.R.attr.textColor };
	// @formatter:on

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private ViewPager pager;

	private int tabCount;

	private int currentPosition = 0;
	private int selectedPosition = 0;
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private int indicatorColor = 0xFF666666;// 滑动指示器颜色
	private int underlineColor = 0x1A000000;// 在视图的底部的全宽度的线颜色
	private int dividerColor = 0x1A000000;// 选项卡之间的分隔颜色

	private boolean shouldExpand = false;// 如果设置为TRUE，每个标签都给予同样的重量，默认为false
	private boolean textAllCaps = true;// 如果为真，所有选项卡标题都是大写，默认为true

	private int scrollOffset = 52;// 卷轴被选择的标签的偏移
	private int indicatorHeight = 8;
	private int underlineHeight = 0;
	private int dividerPadding = 12;
	private int tabPadding = 16;
	private int dividerWidth = 1;
	/**
	 * 指示器距离容器底部的距离,该值不能比容器(LinearLayout的getHeight())-indicatorHeight大,否则无法画出指示器
	 * 用在画指示器canvas.drawRect(lineLeft, height - indicatorHeight, lineRight,
	 * height-underlineBottom, rectPaint);中
	 */
	private int underlineBottom = 0;

	private int tabTextSize = 18;
	private int tabTextColor = 0xFF666666;
	private int selectedTabTextColor = 0xFF666666;// 被选中字体的颜色
	private Typeface tabTypeface = null;// 字体类
	private int tabTypefaceStyle = Typeface.NORMAL;

	private int lastScrollX = 0;

	private int tabBackgroundResId = R.drawable.background_tab;

	private Locale locale;// 语言处理类,多语言处理



	public PagerSlidingTabStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs,
								int defStyle) {
		super(context, attrs, defStyle);

		setFillViewport(true);// 可以令布局填满HorizontalScrollView,当ScrollView里的元素想填满ScrollView时，使用"fill_parent"是不管用的，必需为ScrollView设置：android:fillViewport="true"
		setWillNotDraw(false);// 加入这语句可以触发ondraw方法的执行,在自定义 控件中extend
		// viewGroup默认不执行ondraw.

		tabsContainer = new LinearLayout(context);// 创建tab的布局
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);// 水平布局
		tabsContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));// 设置tab的布局参数
		addView(tabsContainer);// 把tab布局加入

		DisplayMetrics dm = getResources().getDisplayMetrics();// 获取屏幕参数等信息

		scrollOffset = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);// 转变为标准尺寸的一个函数
		indicatorHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		underlineHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		dividerWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		tabTextSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
		underlineBottom = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, underlineBottom, dm);
		// get system attrs (android:textSize and android:textColor)
		// 获取系统的属性样式
		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);


		a.recycle();

		// get custom attrs
		// 获取自定义的属性样式
		a = context.obtainStyledAttributes(attrs,
				R.styleable.PagerSlidingTabStrip);
		tabTextColor =a.getColor(R.styleable.PagerSlidingTabStrip_pstsTabTextColor,tabTextColor);
		// 指示器的颜色
		indicatorColor = a.getColor(
				R.styleable.PagerSlidingTabStrip_pstsIndicatorColor,
				indicatorColor);

		// tab文字选中时的颜色,默认和滑动指示器的颜色一致
		selectedTabTextColor = a.getColor(
				R.styleable.PagerSlidingTabStrip_selectedTabTextColor,
				indicatorColor);
		// 底部全宽线的颜色
		underlineColor = a.getColor(
				R.styleable.PagerSlidingTabStrip_pstsUnderlineColor,
				underlineColor);
		// 选项卡之间的分隔颜色
		dividerColor = a
				.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor,
						dividerColor);
		// 指示器的高度
		indicatorHeight = a.getDimensionPixelSize(
				R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight,
				indicatorHeight);
		// 底部全宽线的高度
		underlineHeight = a.getDimensionPixelSize(
				R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight,
				underlineHeight);

		// 选项卡之间的padding
		dividerPadding = a.getDimensionPixelSize(
				R.styleable.PagerSlidingTabStrip_pstsDividerPadding,
				dividerPadding);

		// tab的padding
		tabPadding = a.getDimensionPixelSize(
				R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight,
				tabPadding);
		// tab的背景资源引用id
		tabBackgroundResId = a.getResourceId(
				R.styleable.PagerSlidingTabStrip_pstsTabBackground,
				tabBackgroundResId);

		// 是否把每个tab都平均分
		shouldExpand = a
				.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand,
						shouldExpand);
		// 滑动偏移量
		scrollOffset = a
				.getDimensionPixelSize(
						R.styleable.PagerSlidingTabStrip_pstsScrollOffset,
						scrollOffset);
		// 首字母是否大写
		textAllCaps = a.getBoolean(
				R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

		underlineBottom = a.getDimensionPixelSize(
				R.styleable.PagerSlidingTabStrip_underlineBottom,
				underlineBottom);

		a.recycle();

		rectPaint = new Paint();// 初始化 画指示器的画笔
		rectPaint.setAntiAlias(true);// 抗锯齿
		rectPaint.setStyle(Style.FILL);// 设置画笔样式,fill为内部填充

		dividerPaint = new Paint();// 初始化 画选项卡直接的线的画笔
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);// 设置画笔宽度,dividerWidth=1

		defaultTabLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

		expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1.0f);// 初始化扩充后的tab布局参数,width=0,height=match_parent,weight=1

		// //初始化多国语言类
		if (locale == null) {
			locale = getResources().getConfiguration().locale;
		}
	}

	// 设置把viewpager set进来的方法
	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}

		pager.setOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void notifyDataSetChanged() {

		tabsContainer.removeAllViews();// 把装载tab的LinearLayout移除所有子视图

		tabCount = pager.getAdapter().getCount();// 获取tab数量

		// 设置tab的标题,padding值,点击事件,然后通过循环把每个tab加入到LinearLayout中
		for (int i = 0; i < tabCount; i++) {

			addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
		}

		// 更新tab中内容的样式
		updateTabStyles();

		// 当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类
		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						// 设置viewpager当前页
						currentPosition = pager.getCurrentItem();
						// 滑动到选择的tab
						scrollToChild(currentPosition, 0);
					}
				});

	}

	// 在tab中添加TextView,并设置值,和一些点击事件
	private void addTextTab(final int position, String title) {

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		addTab(position, tab);
	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);// 获得焦点
		// 设置点击事件
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击设置viewpager当前显示页面
				pager.setCurrentItem(position);
			}
		});
		// 设置tab之间的padding(int left=12, int 0, int right=12, int 0)
		tab.setPadding(tabPadding, 0, tabPadding, 0);
		// 把tab加入到LinearLayout中
		tabsContainer
				.addView(tab, position, shouldExpand ? expandedTabLayoutParams
						: defaultTabLayoutParams);
	}

	/**
	 * 更新tab的样式 设置字体样式
	 */
	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {

			View v = tabsContainer.getChildAt(i);

			v.setBackgroundResource(tabBackgroundResId);

			if (v instanceof TextView) {

				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tab.setTypeface(tabTypeface, tabTypefaceStyle);
				tab.setTextColor(tabTextColor);

				// setAllCaps() is only available from API 14, so the upper case
				// is made manually if we are on a
				// pre-ICS-build
				if (textAllCaps) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						tab.setAllCaps(true);
					} else {
						tab.setText(tab.getText().toString()
								.toUpperCase(locale));
					}
				}
				if (i == selectedPosition) {
					tab.setTextColor(selectedTabTextColor);
				}
			}
		}

	}

	// 滑动到选择的tab
	private void scrollToChild(int position, int offset) {

		if (tabCount == 0) {
			return;
		}

		// 获取LinearLayout的子视图的左边x坐标.+offset(0)
		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			// newScrollX=newScrollX-scrollOffset;
			newScrollX -= scrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/**
		 * 如果在自定义控件的构造函数或者其他绘制相关地方使用系统依赖的代码， 会导致可视化编辑器无法报错并提示： Use
		 * View.isInEditMode() in your custom views to skip code when shown in
		 * Eclipse
		 */

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();

		// draw underline
		rectPaint.setColor(underlineColor);
		canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(),
				height, rectPaint);

		// draw indicator line
		rectPaint.setColor(indicatorColor);

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition);
		float lineLeft = currentTab.getLeft();
		float lineRight = currentTab.getRight();

		// if there is an offset, start interpolating left and right coordinates
		// between current and next tab
		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();

			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
					* lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset)
					* lineRight);
		}

		canvas.drawRect(lineLeft+tabPadding, height - indicatorHeight, lineRight-tabPadding, height
				- underlineBottom, rectPaint);

		// draw divider

		dividerPaint.setColor(dividerColor);
		for (int i = 0; i < tabCount - 1; i++) {
			View tab = tabsContainer.getChildAt(i);
			canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(),
					height - dividerPadding, dividerPaint);
		}
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
								   int positionOffsetPixels) {
			currentPosition = position;
			currentPositionOffset = positionOffset;

			scrollToChild(position, (int) (positionOffset * tabsContainer
					.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(pager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			selectedPosition = position;
			updateTabStyles();
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
		}

	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;

		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);

		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}



	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		notifyDataSetChanged();
	}

	public boolean getShouldExpand() {
		return shouldExpand;
	}

	public boolean isTextAllCaps() {
		return textAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.textAllCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = textColor;
		updateTabStyles();
	}

	public void setTextColorResource(int resId) {
		this.tabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return tabTextColor;
	}

	public void setSelectedTextColor(int textColor) {
		this.selectedTabTextColor = textColor;
		updateTabStyles();
	}

	public void setSelectedTextColorResource(int resId) {
		this.selectedTabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getSelectedTextColor() {
		return selectedTabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
		updateTabStyles();
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPadding;
	}

	public int getUnderlineBottom() {
		return underlineBottom;
	}

	public void setUnderlineBottom(int underlineBottom) {
		this.underlineBottom = underlineBottom;
		invalidate();
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}