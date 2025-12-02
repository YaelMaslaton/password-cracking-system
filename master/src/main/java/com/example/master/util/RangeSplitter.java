package com.example.master.util;

import com.example.master.model.Range;
import java.util.ArrayList;
import java.util.List;

import static com.example.master.common.AppConstants.*;

/**
 * Utility class for splitting phone number ranges into equal parts.
 */
public class RangeSplitter {

    /**
     * Splits a range into equal parts for distribution among Minions.
     * 
     * @param fullStart Start of the full range (e.g., 500000000)
     * @param fullEnd End of the full range (e.g., 599999999)
     * @param numberOfMinions Number of parts to split into
     * @return List of Range objects representing the split ranges
     */
    public static List<Range> splitRange(long fullStart, long fullEnd, int numberOfMinions) {
        List<Range> ranges = new ArrayList<>();
        
        if (numberOfMinions <= ZERO) {
            throw new IllegalArgumentException(ERROR_MINIONS_MUST_BE_POSITIVE);
        }
        
        long totalRange = fullEnd - fullStart + ONE;
        long rangePerMinion = totalRange / numberOfMinions;
        long remainder = totalRange % numberOfMinions;
        
        long currentStart = fullStart;
        
        for (int i = 0; i < numberOfMinions; i++) {
            long currentRangeSize = rangePerMinion + (i < remainder ? ONE : ZERO);
            long currentEnd = currentStart + currentRangeSize - ONE;
            
            ranges.add(new Range(currentStart, currentEnd));
            currentStart = currentEnd + ONE;
        }
        
        return ranges;
    }
}