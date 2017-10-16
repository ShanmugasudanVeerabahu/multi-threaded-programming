package server.bidding.auction;

/**
 *  @author shanmugasudan
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuctionServer {
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected.
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer() {
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance() {
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */

	/*
	 * Statistic variables and server constants: Begin code you should likely
	 * leave alone.
	 */

	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount() {
		return this.soldItemsCount;
	}

	public int revenue() {
		for (Integer i : itemsAndIDs.keySet()) {
			if (!itemsAndIDs.get(i).biddingOpen() && highestBids.containsKey(i)) {
				this.revenue = this.revenue + highestBids.get(i);
			}
		}
		return this.revenue;
	}

	/*
	 * Answer: Invariants: 1. maxBidCount 2. maxSellerItems 3. serverCapacity
	 */

	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at
												// any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items
													// that a seller can submit
													// at any given time.
	public static final int serverCapacity = 80; // The maximum number of active
													// items at a given time.

	/*
	 * Statistic variables and server constants: End code you should likely
	 * leave alone.
	 */

	/**
	 * Some variables we think will be of potential use as you implement the
	 * server...
	 */

	// List of items currently up for bidding (will eventually remove things
	// that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();

	// The last value used as a listing ID. We'll assume the first thing added
	// gets a listing ID of 0.
	private int lastListingID = -1;

	// List of item IDs and actual items. This is a running list with everything
	// ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item. This is a running list
	// with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.
	// This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>();

	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	// Object used for instance synchronization if you need to do it at some
	// point
	// since as a good practice we don't use synchronized (this) if we are doing
	// internal
	// synchronization.
	//
	private static Object instanceLock = new Object();

	/*
	 * The code from this point forward can and should be changed to correctly
	 * and safely implement the methods as needed to create a working
	 * multi-threaded server for the system. If you need to add Object instances
	 * here to use for locking, place a comment with them saying what they
	 * represent. Note that if they just represent one structure then you should
	 * probably be using that structure's intrinsic lock.
	 */

	/* An associative DS to store invalidCounts of seller */
	private HashMap<String, Integer> invalidCountsPerSeller = new HashMap<>();

	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * 
	 * @param sellerName
	 *            Name of the <code>Seller</code>
	 * @param itemName
	 *            Name of the <code>Item</code>
	 * @param lowestBiddingPrice
	 *            Opening price
	 * @param biddingDurationMs
	 *            Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed
	 *         successfully, otherwise -1
	 */
	/*
	 * Answer:
	 * 
	 * @Pre-Conditions: 1. 0 < lowestBiddingPrice < 99 2. biddingDurantionMs > 0
	 * 3. itemsAndIds,itemsUpForBidding,itemsPerSeller,sellerName, itemName !=
	 * null 4. sellerName.isDisqualified == false
	 * 
	 * @Post-Conditions: 1. return value = listingID or -1 *
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs) {
		synchronized (instanceLock) {
			if (itemsUpForBidding.size() == AuctionServer.serverCapacity)
				return -1;
			if (lowestBiddingPrice <= 0 && biddingDurationMs <= 0) {
				return -1;
			}
			if (invalidCountsPerSeller.containsKey(sellerName)) {
				int invalidCounts = invalidCountsPerSeller.get(sellerName);
				if (invalidCounts == 3)
					return -1;
			}
			if (itemsPerSeller.containsKey(sellerName)) {
				int itemCount = itemsPerSeller.get(sellerName);
				if (itemCount == maxSellerItems) {
					return -1;
				}
			}
			if (lowestBiddingPrice < 75 && lowestBiddingPrice > 99) {
				lastListingID++;
				Item item = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
				itemsAndIDs.put(lastListingID, item);
				this.itemsUpForBidding.add(item);
				highestBids.put(lastListingID, lowestBiddingPrice);
				if (!itemsPerSeller.containsKey(sellerName)) {
					itemsPerSeller.put(sellerName, 1);
				} else {
					itemsPerSeller.put(sellerName, 1 + itemsPerSeller.get(sellerName));
				}
				invalidCountsPerSeller.put(sellerName, 0);
				return lastListingID;
			} else if (lowestBiddingPrice < 75 && findMin(itemsUpForBidding)) {
				lastListingID++;
				Item item = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
				itemsAndIDs.put(lastListingID, item);
				this.itemsUpForBidding.add(item);
				highestBids.put(lastListingID, lowestBiddingPrice);
				if (!itemsPerSeller.containsKey(sellerName)) {
					itemsPerSeller.put(sellerName, 1);
				} else {
					itemsPerSeller.put(sellerName, 1 + itemsPerSeller.get(sellerName));
				}
				invalidCountsPerSeller.put(sellerName, 0);
				return lastListingID;
			} else if (lowestBiddingPrice < 75) {
				/*
				 * findMin has returned false, so seller should be disqualified
				 * or counted invalid
				 */
				if (invalidCountsPerSeller.containsKey(sellerName)) {
					int invalidCount = invalidCountsPerSeller.get(sellerName);
					if (invalidCount < 3) {
						invalidCountsPerSeller.put(sellerName, invalidCount + 1);
						return -1;
					}
				}
				invalidCountsPerSeller.put(sellerName, 1);
			}
			return -1;
		}
	}
	/*
	 * Add sellerName, itemCount incremented to itemsPerSeller END IF increment
	 * lastListingID IF 99 > lowestBiddingPrice < 0 OR biddingDurationMs > 0
	 * RETURN -1 END IF CREATE an item with given details Add lastListingId,item
	 * to itemsAndIDs Add item to itemsUpForBidding Add lastListingId,
	 * lowestBiddingPrice in highestBids RETURN lastListingId
	 * 
	 */

	/**
	 * Get all <code>Items</code> active in the auction
	 * 
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	/*
	 * Answer:
	 * 
	 * @Pre-condition: itemsUpForBidding != null
	 * 
	 * @Post-Condition: Return a new Arraylist (a copy of items just to ensure a
	 * clean(thread-safe) read of up-to-date items from the list)
	 */
	public List<Item> getItems() {
		synchronized (instanceLock) {
			List<Item> itemsList = new ArrayList<>();
			for (Item i : itemsUpForBidding) {
				itemsList.add(i);
			}
			return itemsList;
		}
	}

	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * 
	 * @param bidderName
	 *            Name of the <code>Bidder</code>
	 * @param listingID
	 *            Unique ID of the <code>Item</code>
	 * @param biddingAmount
	 *            Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	/*
	 * Answer:
	 * 
	 * @Pre-Conditions: 1. itemsUpForBidding, itemsAndIDs, highestBid,
	 * highestBidders != null 2. listingId > 0
	 * 
	 * @Post-Conditions: return result of submitBid : true or false
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount) {
		synchronized (instanceLock) {
			if (itemsAndIDs.containsKey(listingID)) {
				Item item = itemsAndIDs.get(listingID);
				if (itemsUpForBidding.contains(item) && item.biddingOpen()) {
					if (biddingAmount > highestBids.get(listingID)) {
						if (!highestBidders.containsKey(listingID) && !itemsPerBuyer.containsKey(bidderName))
							itemsPerBuyer.put(bidderName, 1);
						else if (highestBidders.containsKey(listingID)
								&& !highestBidders.get(listingID).equals(bidderName)) {
							Integer itemCount = itemsPerBuyer.get(bidderName);
							if ( itemCount != null && itemCount.intValue() == maxBidCount)
								return false;
							else
								if(itemCount != null)
									itemsPerBuyer.put(bidderName, itemCount + 1);
								else
									itemsPerBuyer.put(bidderName, 1);
						}
						String oldBuyer = highestBidders.get(listingID);
						if(oldBuyer != null){
							int oldBuyerCount = itemsPerBuyer.get(oldBuyer);
							itemsPerBuyer.put(oldBuyer, oldBuyerCount - 1);
						}
						highestBidders.put(listingID, bidderName);
						highestBids.put(listingID, biddingAmount);
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * 
	 * @param bidderName
	 *            Name of <code>Bidder</code>
	 * @param listingID
	 *            Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has
	 *         won<br>
	 *         2 (open) if this <code>Item</code> is still up for auction<br>
	 *         3 (failed) If this <code>Bidder</code> did not win or the
	 *         <code>Item</code> does not exist
	 */
	/*
	 * Answer:
	 * 
	 * @Pre-Conditions: 1. itemsAndIDs, itemsUpForBidding, highestBids,
	 * highestBidders, itemsPerSeller, itemsPerBuyer != null 2. price in
	 * highestBids should be greater than actual price of item
	 * 
	 * @Post-Conditions: result can be Success or Open or Failed
	 */
	public int checkBidStatus(String bidderName, int listingID) {
		synchronized (instanceLock) {
			if (!itemsAndIDs.containsKey(listingID))
				return 3;
			Item item = itemsAndIDs.get(listingID);
			if (!item.biddingOpen()) {
				if (bidderName.equals(highestBidders.get(listingID))) {
					soldItemsCount++;
					itemsUpForBidding.remove(item);
					int buyerCount = itemsPerBuyer.get(bidderName);
					itemsPerBuyer.put(bidderName, buyerCount - 1);
					Integer sellerCount = itemsPerSeller.get(bidderName);
					if(sellerCount != null)
						itemsPerSeller.put(item.seller(), sellerCount.intValue() - 1);
					return 1;
				} else
					return 3;
			}
			return 2;
		}
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * 
	 * @param listingID
	 *            Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been
	 *         made, -1 if no <code>Item</code> exists
	 */
	/*
	 * Answer:
	 * 
	 * @Pre-conditions: 1. highestBids !=null 2. listingID > 0
	 * 
	 * @Post-Conditions: 1.a. Returns itemPrice for given item 1.b If item is
	 * not found return -1
	 */
	public int itemPrice(int listingID) {
		synchronized (highestBids) {
			if (listingID >= 0 && highestBids.containsKey(listingID))
				return highestBids.get(listingID);
			return -1;
		}
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * 
	 * @param listingID
	 *            Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist,
	 *         false otherwise
	 */
	/*
	 * Answer:
	 * 
	 * @Pre-Conditions: 1. listingID > 0 2. itemsUpForBidding, itemsAndIDs,
	 * highestBids != null
	 * 
	 * @Post-Conditions: 1.a return true if the item is unbid or if there is no
	 * item found for the listingID 1.b return false if we find any bids placed
	 * found in highestBids for listingID
	 */
	public Boolean itemUnbid(int listingID) {
		if (itemsAndIDs.containsKey(listingID)) {
			Item item = itemsAndIDs.get(listingID);
			if (itemsUpForBidding.contains(item)) {
				if (item.lowestBiddingPrice() == highestBids.get(listingID)) {
					return true;
				} else
					return false;
			}
			return true;
		}
		return false;
	}

	/*
	 * Function to check if there exists an item in itemsUpForBidding that is
	 * less than lowestBiddingPrice of 75
	 */
	public boolean findMin(List<Item> itemList) {
		// synchronized (instanceLock) {
		for (Item i : itemList) {
			if (i.lowestBiddingPrice() > 75)
				return false;
		}
		return true;
		// }
	}
}
