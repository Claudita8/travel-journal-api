package travel.journal.api.service;

import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;

public interface NoteService {
    NoteDetailsDTO getNoteDetails(int travelId, int noteId);
}
