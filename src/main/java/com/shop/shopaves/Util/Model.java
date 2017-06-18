package com.shop.shopaves.Util;

import com.shop.shopaves.Constant.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by amsyt007 on 21/4/16.
 */
public class Model extends JsonModel {
    public Model(String data) {
        super(data);
    }

    public Model() {
    }

    public Model(JSONObject model) {
        super(model);
    }

    public String getName() {
        return getString(NAME);
    }
    public String getValue() {
        return getString(VALUE);
    }

    public String getComment() {
        return getString(COMMENT);
    }

    public int getCommentCount() {
        return getInt(COMMENTCOUNT);
    }

    public String getIcon() {
        return getString(ICON);
    }

    public int getId() {
        return getInt(ID);
    }

    public int getSenderId() {
        return getInt(SENDER_ID);
    }

    public int getAverageRate() {
        return getInt(AVERAGE_RATE);
    }

    public int getReviewCount() {
        return getInt(REVIEW_COUNT);
    }

    public long getTime() {
        return getLong(TIME);
    }

    public String getTimeDATE() {
        return getString(TIME);
    }

    public int getAverageRating() {
        return getInt(AVERAGE_RATING);
    }

    public String getid() {
        return getString(ID);
    }

    public int getHandlingTime() {
        return getInt(HANDLING_TIME);
    }

    public int getReturns() {
        return getInt(RETURNS);
    }

    public int getPriceValue() {
        return getInt(NamePair.PRICE);
    }

    public Double getItemPriceValue() {
        return getDouble(NamePair.PRICE);
    }

    public int getDiscountValue() {
        return getInt(NamePair.DISCOUNT);
    }

    public Double getItemDiscountValue() {
        return getDouble(NamePair.DISCOUNT);
    }

    public String getDiscount() {
        return getString(NamePair.DISCOUNT);
    }

    public String getAmount() {
        return getString(NamePair.AMOUNT);
    }

    public String getStartDate() {
        return getString(NamePair.START_DATE);
    }

    public String getEndDate() {
        return getString(NamePair.END_DATE);
    }

    public int getRatingValue() {
        return getInt(NamePair.RATING);
    }

    public int getRatingCount() {
        return getInt(NamePair.RATING_COUNT);
    }

    public int getRateCount() {
        return getInt(NamePair.RATE_COUNT);
    }


    public int getCategoryId() {
        return getInt(CATEGORY_ID);
    }

    public int getSubCategoryId() {
        return getInt(SUBCATEGORY_ID);
    }

    public int getBrandId() {
        return getInt(BRAND_ID);
    }

    public String getFeatures() {
        return getString(FEATURES);
    }

    public String getItemName() {
        return getString(ITEMNAME);
    }

    public int getItems() {
        return getInt(ITEMS);
    }

    public String getItemsString() {
        return getString(ITEMS);
    }

    public int getCollections() {
        return getInt(COLLECTIONS);
    }

    public String getPrice() {
        return getString(PRICE);
    }

    public String getCenterX() {
        return getString(CENTERX);
    }

    public String getCenterY() {
        return getString(CENTERY);
    }

    public String getScaleX() {
        return getString(SCALEX);
    }

    public String getScaleY() {
        return getString(SCALEY);
    }

    public String getMinx() {
        return getString(MINX);
    }

    public String getMiny() {
        return getString(MINY);
    }

    public String getMaxx() {
        return getString(MAXX);
    }

    public String getMaxy() {
        return getString(MAXY);
    }

    public String getAngle() {
        return getString(ANGLE);
    }

    public String getQty() {
        return getString(QTY);
    }

    public int getItemQuantity() {
        return getInt(QTY);
    }

    public String getBannerLink() {
        return getString(BANNERLINK);
    }

    public String getLink() {
        return getString(LINK);
    }

    public String getLevel() {
        return getString(LEVEL);
    }

    public String getNextLevel() {
        return getString(NEXT_LEVEL);
    }

    public String getNameOne() {
        return getString(NAME1);
    }

    public String getNameTwo() {
        return getString(NAME2);
    }

    public String getText() {
        return getString(TEXT);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public String getDays() {
        return getString(DAYS);
    }

    public String getCollectionDescription() {
        return getString(DESCRIPTION_ITEM);
    }

    public String getProductDescription() {
        return getString(DESCRIPTION_ITEM);
    }

    public String getDescriptionItem() {
        return getString(DESCRIPTION_ITEM);
    }

    public String getSalePrice() {
        return getString(SALE_PRICE);
    }

    public String getItemSalePrice() {
        return getString(SALE_PRICE);
    }

    public String getMRP() {
        return getString(MRP);
    }

    public String getRating() {
        return getString(RATING);
    }

    public String getCoupon() {
        return getString(COUPON);
    }

    public String getMessage() {
        return getString(MESSAGE);
    }

    public String getStatus() {
        return getString(STATUS);
    }

    public String getCode() {
        return getString(CODE);
    }

    public String getPinCode() {
        return getString(PINCODE);
    }

    public String getParentCat() {
        return getString(PARENT_CAT);
    }

    public int getParentId() {
        return getInt(PARENT_ID);
    }

    public Double getLattitude() {
        return getDouble(LATTITUDE);
    }

    public Double getLongitude() {
        return getDouble(LONGITUDE);
    }

    public String getShipId() {
        return getString(SHIPID);
    }

    public int getItemStatus() {
        return getInt(STATUS);
    }

    public int getLikeStatus() {
        return getInt(LIKESTATUS);
    }

    public int getOrderId() {
        return getInt(ORDER_ID);
    }

    public int getStateId() {
        return getInt(STATE_ID);
    }

    public int getCityId() {
        return getInt(CITY_ID);
    }

    public int getProductLikeStatus() {
        return getInt(LIKE_STATUS);
    }

    public int getLikesStatus() {
        return getInt(LIKE_STATUS);
    }

    public String getFileName() {
        return getString(FILE_NAME);
    }

    public String getData() {
        return getString(DATA);
    }

    public String getDTime() {
        return getString(DTIME);
    }

    public String getSCharge() {
        return getString(SCHARGES);
    }

    public String getGroup() {
        return getString(GROUP);
    }

    public String getProfile() {
        return getString(PROFILE);
    }

    public String getProductLocationa() {
        return getString(PRODUCT_LOCATION);
    }

    public String getBrandInfo() {
        return getString(BRAND);
    }

    public String getBackBgData() {
        return getString(BACKGROUND_BG);
    }

    public String getTimeStamp() {
        return getString(TIME_STAMP);
    }

    public String getTimeStampSmall() {
        return getString(TIMESTAMP);
    }

    public Boolean isCurrent() {
        return getBool(CURRENT);
    }


    public String getSubCatJsonArray() {
        return getString(SUBCATEGORY);
    }

    public String getShippingType() {
        return getString(SHIPPING_TYPE);
    }

    public String getShippingDays() {
        return getString(SHIPPING_DAYS);
    }

    public int getShippingCost() {
        return getInt(SHIPPING_COST);
    }

    public Boolean isShippingfree() {
        return getBool(IS_SHIPPING_FREE);
    }

    public String getParentCategory() {
        return getString(PARENTCAT);
    }

    public String getColorCode() {
        return getString(COLOR_CODE);
    }

    public String getImage() {
        return getString(IMAGE);
    }

    public String getImageProperties() {
        return getString(IMAGEPROPERTIES);
    }

    public String getEditProductImage() {
        return getString(EDIT_PRODUCT_IMAGE);
    }

    public String getUserProfileImage() {
        return getString(USER_PROFILE_IMAGE);
    }

    public String getImageUrl() {
        return getString(IMAGE_URL);
    }

    public String getUserImage() {
        return getString(USER_IMAGE);
    }

    public String getProductName() {
        return getString(PRODUCT_NAME);
    }

    public String getPaymentType() {
        return getString(PAYMENT_TYPE);
    }

    public String getType() {
        return getString(TYPE);
    }

    public String getStreetAddress() {
        return getString(STREET_ADDRESS);
    }

    public String getAddress() {
        return getString(ADDRESS);
    }

    public String getAddressID() {
        return getString(ADDRESS_ID);
    }

    public String getAddressLine2() {
        return getString(ADDRESSLINE2);
    }

    public String getResult() {
        return getString(RESULT);
    }

    public String getEmail() {
        return getString(EMAIL);
    }

    public String getEmailId() {
        return getString(EMAIL_ID);
    }

    public String getMobileNumber() {
        return getString(MOBILE_NUMBER);
    }

    public String getMobile() {
        return getString(MOBILE);
    }

    public String getCity() {
        return getString(CITY);
    }

    public String getState() {
        return getString(STATE);
    }

    public String getCountry() {
        return getString(COUNTRY);
    }

    public String getZipCode() {
        return getString(ZIPCODE);
    }

    public boolean getCurrent() {
        return getBool(CURRENT);
    }

    public String getDateOfBirth() {
        return getString(DOB);
    }

    public String getProductImage() {
        return getString(PRODUCT_IMAGE);
    }

    public String getShippingCharges() {
        return getString(SHIPPING_CHARGE);
    }

    public String getError() {
        return getString(ERROR);
    }

    public String getTrackingNumber() {
        return getString(TRACKING_NUMBER);
    }

    public int getLikes() {
        return getInt(LIKES);
    }

    public int getLikeCount() {
        return getInt(LIKE_COUNT);
    }

    public String getCondition() {
        return getString(CONDITION);
    }

    public String getUser() {
        return getString(USER);
    }

    public String getBannerImage() {
        return getString(BANNER);
    }

    public int getUserId() {
        return getInt(USERID);
    }

    public int getProductUserId() {
        return getInt(PRODUCT_USERID);
    }

    public String getUserIdString() {
        return getString(USERID);
    }

    public int getProductId() {
        return getInt(PRODUCT_ID);
    }

    public int getFollowers() {
        return getInt(FOLLOWERS);
    }

    public int getFollowings() {
        return getInt(FOLLOWING);
    }

    public boolean isFollow() {
        return getBool(FOLLOW);
    }

    public boolean hasSubCat() {
        return getBool(HAS_SUBCAT);
    }

    public int getProductCommentCount() {
        return getInt(COMMENT_COUNT);
    }

    public int getCurrentLikeStatus() {
        return getInt(CURRENT_LIKE_STATUS);
    }

    public int getCatId() {
        return getInt(CAT_ID);
    }

    public int getCountryId() {
        return getInt(COUNTRY_ID);
    }


    public String getUserName() {
        return getString(NamePair.USER_NAME);
    }

    public String getFirstName() {
        return getString(NamePair.FIRST_NAME);
    }

    public String getLastName() {
        return getString(NamePair.LAST_NAME);
    }

    public String getSize() {
        return getString(NamePair.SIZE);
    }

    public String getLength() {
        return getString(NamePair.LENGTH);
    }

    public String getWeight() {
        return getString(NamePair.WEIGHT);
    }

    public String getWidth() {
        return getString(NamePair.WIDTH);
    }

    public String getHeight() {
        return getString(NamePair.HEIGHT);
    }

    public Boolean getIsPublish() {
        return getBool(NamePair.IS_PUBLISH);
    }

    public Boolean hasOffer() {
        return getBool(NamePair.HAS_OFFER);
    }

    public boolean isParentCatNull() {
        return isKeyNull(PARENT_CAT);
    }

    public Model[] getTagArray() {
        return getArray(getString(TAGS));
    }

    public Model[] getProductArray() {
        return getArray(getString(PRODUCTS));
    }

    public Model[] getProductArr() {
        return getArray(getString(PRODUCT));
    }

    public Model[] getProducSpc() {
        return getArray(getString(SPC));
    }

    public Model[] getCityArray() {
        return getArray(getString(CITIES));
    }

    public Model[] getStyleArray() {
        return getArray(getString(STYLES));
    }

    public Model[] getColorArray() {
        return getArray(getString(COLORS));
    }

    public Model[] getCategoryArray() {
        return getArray(getString(CATEGORIES));
    }

    public Model[] getSpecificationArray() {
        return getArray(getString(SPECIFICATIONS));
    }

    public Model[] getValuesArray() {
        return getArray(getString(VALUES));
    }

    public Model[] getFlatArray() {
        return getArray(getString(FLAT));
    }

    public Model[] getCalculatedArray() {
        return getArray(getString(CALCULATED));
    }

    public Model[] getCommentsArray() {
        return getArray(getString(COMMENTS));
    }

    public Model[] getCollectionArray() {
        return getArray(getString(COLLECTION));
    }

    public Model[] getStoresArray() {
        return getArray(getString(STORES));
    }

    public Model[] getCategoryDataArray() {
        return getArray(getString(CATEGORY));
    }

    public Model[] getImageArray() {
        return getArray(getString(IMAGE));
    }

    public Model[] getBrandsArray() {
        return getArray(getString(BRANDS));
    }

    public Model[] getAddsLargeArray() {
        return getArray(getString(ADDS_LARGE));
    }

    public Model[] getShopBrandsArray() {
        return getArray(getString(SHOP_BRANDS));
    }

    public Model[] getShopCategoryArray() {
        return getArray(getString(SHOP_CATEGORY));
    }

    public Model[] getShopAddsSmallArray() {
        return getArray(getString(SHOP_SMALL_ADDS));
    }

    public Model[] getDealARR() {
        return getArray(getString(DEAL));
    }

    public Model[] getShopFashionTrendsArray() {
        return getArray(getString(SHOP_FASHION_TRENDS));
    }

    public Model[] getShopNewProductArray() {
        return getArray(getString(SHOP_NEW_PRODUCT));
    }

    public Model[] getShopHotProductArray() {
        return getArray(getString(SHOP_HOT_PRODUCT));
    }

    public Model[] getProductViewedArray() {
        return getArray(getString(PRODUCT_VIEWED));
    }

    public Model[] getSimilarProductArray() {
        return getArray(getString(SIMILARS));
    }

    public Model[] getRecentlyViewedProductArray() {
        return getArray(getString(RECENTLY_VIEWED));
    }

    public Model[] getReviewProductArray() {
        return getArray(getString(REVIEWS));
    }

    public Model[] getCollectionsArray() {
        return getArray(getString(COLLECTIONS));
    }

    public Model[] getSubCategoryDataArray() {
        return getArray(getString(SUBCATEGORY));
    }

    public Model[] getSpcfArray() {
        return getArray(getString(SPCF));
    }

    public Model[] getDataArray() {
        return getArray(getString(DATA));
    }

    public Model[] getNewArrivalArray() {
        return getArray(getString(NEW_ARRIVAL));
    }

    public Model[] getMostPopularArray() {
        return getArray(getString(Constants.MOST_POPULATED));
    }


    public Model[] getAssignTasks(String array) {
        return getArray(array);
    }


    public JSONArray getJsonImageArray() {
        return getJSONArray(ARRAY_IMG);
    }

    public JSONArray getJsonFlatArray() {
        return getJSONArray(FLAT);
    }

    public JSONArray getJsonCalculatedArray() {
        return getJSONArray(CALCULATED);
    }

    public JSONArray getCategoryIdArray() {
        return getJSONArray(CATEGORY_IDS);
    }

    public JSONArray getDataJsonArray() {
        return getJSONArray(DATA);
    }

    public JSONArray getJsonValuesArray() {
        return getJSONArray(VALUES);
    }

    public JSONArray getBrandIdsArray() {
        return getJSONArray(BRAND_IDS);
    }


}
